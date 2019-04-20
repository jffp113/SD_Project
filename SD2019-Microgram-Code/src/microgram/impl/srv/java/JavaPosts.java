package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.*;

import java.net.URI;
import java.util.*;

import kakfa.KafkaPublisher;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import utils.Hash;
import utils.IP;

import java.util.concurrent.ConcurrentHashMap;

public class JavaPosts implements Posts {

	/**
	 * Tag used by the topics related with the messages JavaPost publishes.
	 */
	public static final String JAVA_POST_EVENTS = "Microgram-JavaPosts";

	/**
	 * Keys used by the messages JavaPosts publishes.
	 */
	public enum PostsEventKeys {
		CREATE,DELETE,CREATE_FAIL
	};

	/**
	 * Maps posts to by their id. Supports concurrency. 
	 */
	protected Map<String, Post> posts = new ConcurrentHashMap<>(new HashMap<>());
	
	/**
	 * Maps a userId to the ids of the posts he/she likes. Supports concurrency.
	 */
	protected Map<String, Set<String>> likes = new ConcurrentHashMap<>(new HashMap<>());
	
	/**
	 * Maps a userId to his/her posts' ids. Supports concurrenncy. 
	 */
	protected Map<String, Set<String>> userPosts = new ConcurrentHashMap<>(new HashMap<>());

	/**
	 * Publishes messages.
	 */
	private KafkaPublisher publisher;
	
	/**
	 * Allows this server to contact others.
	 */
	private final ServerInstantiator si = new ServerInstantiator();

	public JavaPosts() {
		initializeKafka();
	}

	private void initializeKafka(){
		publisher = new KafkaPublisher();
	}

	public Result<Post> getPost (String postId) {
		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
		return error(NOT_FOUND);
	}

	private void deleteImageNotification(Post post) {
		String message = String.format("%s %s %s %s", IP.hostAddress(),post.getPostId(),post.getOwnerId(),post.getMediaUrl());
		publisher.publish(JAVA_POST_EVENTS,PostsEventKeys.DELETE.name(),message);
		System.out.println("DELETE_NOTIFICATION");
	}

	public Result<Void> deletePost (String postId) {
		Post postRemoved = posts.remove(postId);

		if(postRemoved == null)
			return error(NOT_FOUND);

		//Remove all users that Liked The post
		likes.remove(postId);
		
		//Remove the post from the user who posted
		String userId = postRemoved.getOwnerId();
		Set<String> uPosts = this.userPosts.get(userId);
		uPosts.remove(postRemoved.getPostId());

		deleteImageNotification(postRemoved);

		return ok();
	}

	private void postCreatedNotification(Post post) {
		String message = String.format("OK %s %s %s", IP.hostAddress(),post.getPostId(),post.getOwnerId());
		publisher.publish(JAVA_POST_EVENTS,PostsEventKeys.CREATE.name(),message);
		System.out.println("CREATE_NOTIFICATION_POST_OK");
	}

	private void postNotCreatedNotification(Post post){
		String message = String.format("%s %s %s",IP.hostAddress(),post.getPostId(),post.getMediaUrl());
		publisher.publish(JAVA_POST_EVENTS,PostsEventKeys.CREATE_FAIL.name(),message);
		System.out.println("CREATE_NOTIFICATION_POST_CONFLICT");
	}
	
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {
			System.out.println( System.currentTimeMillis() + " Creating Post from user: " + post.getOwnerId());
			post.setPostId(postId);
			likes.put(postId, new HashSet<>());
			
			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = Collections.synchronizedSet(new LinkedHashSet<>()));
			posts.add(postId);

			postCreatedNotification(post);
			return ok(postId);
		}
		else {
			postNotCreatedNotification(post);
			return error(CONFLICT);
		}
	}
	
	public Result<Void> like (String postId, String userId, boolean isLiked) {
		Set<String> res = likes.get(postId);
		if (res == null)
			return error( NOT_FOUND );

		if (isLiked) {
			if (!res.add(userId))
				return error( CONFLICT );
		} else {
			if (!res.remove(userId))
				return error( NOT_FOUND );
		}

		getPost(postId).value().setLikes(res.size());
		return ok();
	}

	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);
		
		if (res != null)
			return ok(res.contains(userId));
		else
			return error( NOT_FOUND );
	}
	
	@Override
	public Result<List<String>> getPosts (String userId) {
		System.out.println("Getting post From: " + userId);
		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
		
			return ok( new LinkedList<>() ); // See if the TEST Change TODO
	}
	
	@Override
	public Result<List<String>> getFeed(String userId) {
			Result<Set<String>> reply;
			Set<String> following;
			List<String> result = new LinkedList<>();

			System.out.println("Getting feed for user " + userId);
			reply = this.si.profiles(0).getFollowing(userId);

			System.out.println(reply.error());

			if (!reply.isOK())
				return error(NOT_FOUND);

			following = reply.value();

			System.out.println(following);
			following
					.forEach(f ->{
									Set<String> p = userPosts.get(f);
									if(p != null)
									result.addAll(p);
					});

			return ok(result);

	}
    
    public Result<Void> removeAllPostsFromUser(String userId) {
		System.out.println("Start Removing all Posts from " + userId);
    	Set<String> userSetPosts = userPosts.get(userId);

		if(userSetPosts == null)
			return error(NOT_FOUND);

		for(String postId :userSetPosts)
			this.deletePost(postId);

	    return ok();
    }
}
