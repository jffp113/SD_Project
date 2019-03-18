package microgram.impl.clt.java;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;

public class _TODO_RetryPostsClient extends RetryClient implements Posts {

	final Posts impl;
	
	public _TODO_RetryPostsClient( Posts impl ) {
		this.impl = impl;
	}

	@Override
	public Result<Post> getPost(String postId) {
		return reTry( () -> impl.getPost(postId));
	}

	@Override
	public Result<String> createPost(Post post) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deletePost(String postId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
