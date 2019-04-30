package microgram.impl.clt.java;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;

public class RetryPostsClient extends RetryClient implements Posts {

	private final Posts impl;
	
	public RetryPostsClient( Posts impl ) {
		this.impl = impl;
	}

	@Override
	public Result<Post> getPost(String postId) {
		return super.reTry( () -> impl.getPost(postId));
	}

	@Override
	public Result<String> createPost(Post post) {
		return super.reTry(() -> impl.createPost(post));
	}

	@Override
	public Result<Void> deletePost(String postId) {
		return super.reTry(() -> impl.deletePost(postId));
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		return super.reTry(() -> impl.like(postId, userId, isLiked));
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		return super.reTry(() -> impl.isLiked(postId, userId));
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		return super.reTry(() -> impl.getPosts(userId));
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		return super.reTry(() -> impl.getFeed(userId));
	}

	@Override
	public Result<Void> removeAllPostsFromUser(String userId) {
		return super.reTry(() -> impl.removeAllPostsFromUser(userId));
	}
	
}
