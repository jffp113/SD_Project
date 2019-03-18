package microgram.impl.srv.rest;

import java.net.URI;
import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.rest.RestPosts;
import microgram.impl.srv.java.JavaPosts;

// Make this class concrete.
public class _TODO_RestPostsResources extends RestResource implements RestPosts {

	final Posts impl;
		
	public _TODO_RestPostsResources(URI serverUri) {
		this.impl = new JavaPosts();
	}
	
	@Override
	public Post getPost(String postId) {
		return super.resultOrThrow(impl.getPost(postId));
	}

	@Override
	public void deletePost(String postId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createPost(Post post) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLiked(String postId, String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void like(String postId, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getPosts(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getFeed(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
 
}
