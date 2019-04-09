package microgram.api.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import microgram.api.Post;

/**
 * REST API of the Posts service 
 * 
 * @author smd
 *
 */
@Path( RestPosts.PATH )
public interface RestPosts {

	static final String PATH = "/posts";
	
	//METHODS PATH CONSTANT COMPONENTS
	
	static final String IS_LIKED_PATH_CONSTANT_COMPONENT =
			"likes";
	
	static final String LIKE_PATH_CONSTANT_COMPONENT =
			"likes";
	
	static final String GET_POSTS_PATH_CONSTANT_COMPONENT =
			"from";
	
	static final String GET_FEED_PATH_CONSTANT_COMPONENT =
			"feed";
	
	static final String REMOVE_ALL_POSTS_PATH_CONSTANT_COMPONENT =
			"allPosts";
	
	//METHODS PATH VARIABLES
	
	static final String POST_ID = "postId";
	
	static final String USER_ID = "userId";
	
	
	//METHODS PATH
	
	static final String GET_POST_PATH =
			"/{" + POST_ID + "}";
	
	static final String DELETE_POST_PATH =
			"/{" + POST_ID + "}";
	
	static final String IS_LIKED_PATH =
			"/{" + POST_ID + "}/" + 
			IS_LIKED_PATH_CONSTANT_COMPONENT +
			"/{" + USER_ID + "}";
	
	static final String LIKE_PATH =
			"/{" + POST_ID + "}/" +
			LIKE_PATH_CONSTANT_COMPONENT + 
			"/{" + USER_ID + "}";
	
	static final String GET_POSTS_PATH =
			"/" + GET_POSTS_PATH_CONSTANT_COMPONENT +
			"/{" + USER_ID + "}";
	
	static final String GET_FEED_PATH =
			"/" + GET_FEED_PATH_CONSTANT_COMPONENT +
			"/{" + USER_ID + "}";
	
	static final String REMOVE_ALL_POSTS_FROM_USER_PATH =
			"/" + REMOVE_ALL_POSTS_PATH_CONSTANT_COMPONENT +
			"/{" + USER_ID + "}";
	
	@GET
	@Path(GET_POST_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	Post getPost( @PathParam(POST_ID) String postId );
		
	@DELETE
	@Path(DELETE_POST_PATH)
	void deletePost( @PathParam(POST_ID) String postId );
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	String createPost( Post post );

	@GET
	@Path(IS_LIKED_PATH)
	boolean isLiked( @PathParam(POST_ID) String postId, @PathParam(USER_ID) String userId);

	@PUT
	@Path(LIKE_PATH)	
	@Consumes(MediaType.APPLICATION_JSON)
	void like( @PathParam(POST_ID) String postId, @PathParam(USER_ID) String userId, boolean isLiked);
		
	@GET
	@Path(GET_POSTS_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	List<String> getPosts( @PathParam(USER_ID) String userId);
	
	@GET
	@Path(GET_FEED_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	List<String> getFeed(@PathParam(USER_ID) String userId);

	@DELETE
	@Path(REMOVE_ALL_POSTS_FROM_USER_PATH)
	void removeAllPostsFromUser(@PathParam(USER_ID) String userId);
}
