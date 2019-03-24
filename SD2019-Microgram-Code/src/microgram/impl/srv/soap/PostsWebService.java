package microgram.impl.srv.soap;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapPosts;
import microgram.impl.srv.java.JavaPosts;

//We implemented
public class PostsWebService extends SoapService implements SoapPosts {

	final Posts impl;
	
	protected PostsWebService() {
		this.impl = new JavaPosts(null,null);
	}
	@Override
	public Post getPost( String postId ) throws MicrogramException {
		return super.resultOrThrow( impl.getPost(postId));
	}

	@Override
	public void deletePost(String postId) throws MicrogramException {
		super.resultOrThrow(impl.deletePost(postId));
	}

	@Override
	public String createPost(Post post) throws MicrogramException {
		return super.resultOrThrow(impl.createPost(post));
	}

	@Override
	public boolean isLiked(String postId, String userId) throws MicrogramException {
		return super.resultOrThrow(impl.isLiked(postId, userId));
	}

	@Override
	public void like(String postId, String userId, boolean isLiked) throws MicrogramException {
		super.resultOrThrow(impl.like(postId, userId, isLiked));
	}

	@Override
	public List<String> getPosts(String userId) throws MicrogramException {
		return super.resultOrThrow(impl.getPosts(userId));
	}

	@Override
	public List<String> getFeed(String userId) throws MicrogramException {
		return super.resultOrThrow(impl.getFeed(userId));
	}
	
}
