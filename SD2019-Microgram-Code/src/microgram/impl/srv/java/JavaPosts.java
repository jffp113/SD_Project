package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import microgram.api.Post;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import utils.Hash;
import java.util.concurrent.ConcurrentHashMap;

public class JavaPosts implements Posts {

	protected Map<String, Post> posts = 
			new ConcurrentHashMap<>(new HashMap<>());
	protected Map<String, Set<String>> likes = 
			new ConcurrentHashMap<>(new HashMap<>());
	protected Map<String, Set<String>> userPosts =
			new ConcurrentHashMap<>(new HashMap<>());

	/*
	 * Should sets be concurrent too?
	 * Does ConcurrentMaps destroy parallelism?
	 * 
	 * */

	
	private Profiles[] profiles;
	private Media[] media;
	private Posts[] postClients;
	
	/*TODO
	 * Check if this is legal 
	 */
	public JavaPosts() {
		super();
		this.profiles = null;
		this.posts = null;
		this.media = null;
	}
	
	@Override
	public Result<Post> getPost(String postId) {
		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
			return error(NOT_FOUND);
	}
	
	
	private Media media() {
		if(media == null) {
			synchronized (this) {
				if(media == null) {
					this.media = ClientFactory.buildMedia();
				}
			}
		}
		return media[0];
	}
	
	private Profiles profiles() {
		if(profiles == null) {
			synchronized (this) {
				if(profiles == null) {
					this.profiles = ClientFactory.buildProfile();
				}
			}
		}
		return profiles[0];
	}
	
	private Profiles posts() {
		if(profiles == null) {
			synchronized (this) {
				if(profiles == null) {
					this.profiles = ClientFactory.buildProfile();
				}
			}
		}
		return profiles[0];
	}
	
	//We implemented
	/*
	 * TODO: Communicate with RestStorageServer to delete the image associated with the Post
	 */
	@Override
	public Result<Void> deletePost(String postId) {
	
		Post postRemoved = posts.remove(postId);
		if(postRemoved == null)
			error(NOT_FOUND);
		
		
		//Remove all users that Liked The post
		likes.remove(postId);
		
		//Remove the post from the user who posted
		String userId = postRemoved.getOwnerId();
		Set<String> uPosts = this.userPosts.get(userId);
		uPosts.remove(postRemoved.getPostId());
		
		//Remove the image associated with the Post (Check if can do this)
		Result<Void> r = media().delete(postRemoved.getMediaUrl());
		if(!r.isOK())
			return  error(ErrorCode.INTERNAL_ERROR);
		
		return ok();
	}

	
	@Override
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, new HashSet<>());

			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = new LinkedHashSet<>());
			posts.add(postId);

			return ok(postId);
		}
		else
			return error(CONFLICT);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		
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
		Result<Set<String>> reply = null;
		Set<String> following = null;
		List<String> result = new LinkedList<String>();
		
		reply = profiles().getFollowing(userId);
		
		if(!reply.isOK())
			return  error(ErrorCode.INTERNAL_ERROR);
		
		following = reply.value();
		
		following
			.forEach(f -> result.addAll(userPosts.get(f)));
		
		return ok(result);
	}
}
