package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.*;

import java.util.*;

import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import microgram.impl.srv.rest.PostsRestServer;
import utils.Hash;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaPosts implements Posts {

	private static Logger Log = Logger.getLogger(JavaPosts.class.getName());

	protected Map<String, Post> posts = 
			new ConcurrentHashMap<>(new HashMap<>());
	protected Map<String, Set<String>> likes = 
			new ConcurrentHashMap<>(new HashMap<>());
	protected Map<String, Set<String>> userPosts =
			new ConcurrentHashMap<>(new HashMap<>());

	/*
	 * Should sets be concurrent too?
	 * Do ConcurrentMaps destroy parallelism?
	 * 
	 * */

	static{
		Log.setLevel( Level.FINER );
		Log.info("Initiated JavaPost class teste\n");
	}


	private Profiles[] profiles;
	private Media[] media;
	private Posts[] postClients;
	
	/*TODO
	 * Check if this is legal 
	 */
	public JavaPosts() {
		super();
	}
	
	private Media media() {
		if(media == null) {
			synchronized (this) {
				if(media == null) {
					try {
						this.media = ClientFactory.buildMedia();
					} catch (NoServersAvailableException e){
						this.media = null;
					}
				}
			}
		}
		return media[0];
	}
	
	private Profiles profiles() {
		Log.info("JavaPosts: profile() invoked\n");
		if(profiles == null) {
			synchronized (this) {
				if(profiles == null) {
					try{
					this.profiles = ClientFactory.buildProfile();
					} catch (NoServersAvailableException e){
						this.profiles = null;
					}
				}
			}
		}
		return profiles[0];
	}
	
	private Posts posts() {
		if(postClients == null) {
			synchronized (this) {
				if(postClients == null) {
					try {
						this.postClients = ClientFactory.buildPosts();
					}catch (NoServersAvailableException e){
						this.postClients = null;
					}
				}
			}
		}
		return postClients[0];
	}

	@Override
	public Result<Post> getPost(String postId) {
		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
		return error(NOT_FOUND);
	}

	//We implemented
	/*
	 * TODO: Communicate with RestStorageServer to delete the image associated with the Post
	 */
	@Override
	public Result<Void> deletePost(String postId) {
		Post postRemoved = posts.remove(postId);

		if(postRemoved == null)
			return error(NOT_FOUND);
		
		
		//Remove all users that Liked The post
		likes.remove(postId);
		
		//Remove the post from the user who posted
		String userId = postRemoved.getOwnerId();
		Set<String> uPosts = this.userPosts.get(userId);
		uPosts.remove(postRemoved.getPostId());
		
		//Remove the image associated with the Post (Check if can do this)
		Result<Void> r = media().delete(postRemoved.getMediaUrl());
		/*if(!r.isOK())
			return  error(ErrorCode.INTERNAL_ERROR);*/

		return ok();
	}

	
	@Override
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {

			post.setPostId(postId);
			likes.put(postId, new HashSet<>());
			
			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = Collections.synchronizedSet(new LinkedHashSet<>()));
			posts.add(postId);

			return ok(postId);
		}
		else
			return error(CONFLICT);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		Log.info("Like " + postId + " " + userId +  " " + isLiked + "\n") ;
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

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);
		
		if (res != null)
			return ok(res.contains(userId));
		else
			return error( NOT_FOUND );
	}
	
	@Override
	public Result<List<String>> getPosts(String userId) {
		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
			return error( NOT_FOUND );
	}
	
	//We implemented
	@Override
	public Result<List<String>> getFeed(String userId) {
		Result<Set<String>> reply;
		Set<String> following;
		List<String> result = new LinkedList<>();

		reply = profiles().getFollowing(userId);

		if(!reply.isOK())
			return  error(NOT_FOUND);
		
		following = reply.value();
		
		following
			.forEach(f -> result.addAll(userPosts.get(f)));
		
		return ok(result);
	}

	//Code Review TODO
    public Result<Void> removeAllPostsFromUser(String userId){
		Set<String> userSetPosts = userPosts.get(userId);

		if(userSetPosts == null)
			return error(NOT_FOUND);

		for(String postId :userSetPosts)
			this.deletePost(postId);

	    return ok();
    }
}
