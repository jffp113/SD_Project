package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

public class JavaPosts implements Posts {

	protected Map<String, Post> posts = new HashMap<>();
	protected Map<String, Set<String>> likes = new HashMap<>();
	protected Map<String, Set<String>> userPosts = new HashMap<>();

	private Profiles profiles;
	private Media media;
	
	/*TODO
	 * Check if this is legal 
	 */
	public JavaPosts(Profiles profiles,Media media) {
		this.profiles = profiles;
		this.media = media;
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
			error(NOT_FOUND);
		
		
		//Remove all users that Liked The post
		likes.remove(postId);
		
		//Remove the post from the user who posted
		String userId = postRemoved.getOwnerId();
		Set<String> uPosts = this.userPosts.get(userId);
		uPosts.remove(postRemoved.getPostId());
		
		//Remove the image associated with the Post (Check if can do this)
		if(media == null)
			return  error(ErrorCode.INTERNAL_ERROR);
		Result<Void> r = media.delete(postRemoved.getMediaUrl());
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
	/*Ask Teacher about creating a client to communicate with
	 * ProfileServer Rest Or Soap in order to get the users that the userId is following,
	 * 
	 * */
	@Override
	public Result<List<String>> getFeed(String userId) {
		Set<String> userPosts = this.userPosts.get(userId);
		
		if(userPosts == null)
			error(NOT_FOUND);
		
		return ok(new ArrayList<>(userPosts));
	}
}
