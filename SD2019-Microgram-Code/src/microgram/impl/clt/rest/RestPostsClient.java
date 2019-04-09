package microgram.impl.clt.rest;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.rest.RestPosts;

import static microgram.api.rest.RestPosts.LIKE_PATH_CONSTANT_COMPONENT;
import static microgram.api.rest.RestPosts.IS_LIKED_PATH_CONSTANT_COMPONENT;
import static microgram.api.rest.RestPosts.GET_FEED_PATH_CONSTANT_COMPONENT;
import static microgram.api.rest.RestPosts.REMOVE_ALL_POSTS_PATH_CONSTANT_COMPONENT;

//TODO We implemented
public class RestPostsClient extends RestClient implements Posts {

	public RestPostsClient(URI serverUri) {
		super(serverUri, RestPosts.PATH);
	}


	public Result<Post> getPost(String postId){
		Response r = target.path(postId)
							.request()
							.get();
		
		return super.responseContents(r, Status.OK, new GenericType<Post>() {});
	}
	
	public Result<String> createPost(Post post) {
		Response r = target
				.request()
				.post( Entity.entity( post, MediaType.APPLICATION_JSON));
		
		return super.responseContents(r, Status.OK, new GenericType<String>(){});	
	}


	@Override
	public Result<Void> deletePost(String postId) {
		Response r = target.path(postId)
							.request()
							.delete();
		
		return super.verifyResponse(r, Status.NO_CONTENT);
	}


	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		Response r = target
				.path(postId)
				.path(LIKE_PATH_CONSTANT_COMPONENT)
				.path(userId)
				.request()
				.put( Entity.entity(isLiked, MediaType.APPLICATION_JSON));
		
		return super.verifyResponse(r, Status.NO_CONTENT);
	}


	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Response r = target
				.path(postId)
				.path(IS_LIKED_PATH_CONSTANT_COMPONENT)
				.path(userId)
				.request()
				.get();	
		
		return super.responseContents(r, Status.OK,new GenericType<Boolean>(){});
	}


	@Override
	public Result<List<String>> getPosts(String userId) {
		Response r = target
				.path(userId)
				.request()
				.get();
		
		
		return super.responseContents(r, Status.OK, new GenericType<List<String>>(){});
	}


	@Override
	public Result<List<String>> getFeed(String userId) {
		Response r = target
				.path(GET_FEED_PATH_CONSTANT_COMPONENT)
				.path(userId)
				.request()
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<List<String>>(){});
	}


	public Result<Void> removeAllPostsFromUser(String userId){
		Response r = target
				.path(REMOVE_ALL_POSTS_PATH_CONSTANT_COMPONENT)
				.path(userId)
				.request()
				.delete();

		return super.verifyResponse(r,Status.NO_CONTENT);
	}
}
