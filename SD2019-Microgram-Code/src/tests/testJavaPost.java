 package tests;

import java.net.URI;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Result;
import microgram.api.rest.RestPosts;
import microgram.impl.clt.rest.RestPostsClient;
import microgram.impl.srv.java.JavaPosts;
import microgram.impl.srv.rest.PostsRestServer;
import microgram.impl.srv.rest.RestPostsResources;

public class testJavaPost {
	public static void main(String[] args) {
		/*URI[] uris = Discovery.findUrisOf(PostsRestServer.SERVICE, 1);
		
		if(uris.length == 0)
			System.err.println("No Servers Found");
				
		RestPostsClient client = new RestPostsClient(uris[0]);
		
		Result<String> resultPost = client.createPost(new Post(null, "13456", "no-media", "Lisboa", 1222, 0));
		System.out.println(resultPost.value());
		
		Result<Post> post = client.getPost(resultPost.value());
		
		System.out.println( post.value().getPostId()+  " " + post.value().getOwnerId() + " " + post.value().getTimestamp());
		
		System.out.println("Delete " + client.deletePost(resultPost.value()).error());
		
		System.out.println();
		//...*/
		
		
	}
}
