package microgram.impl.clt.soap;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.soap.SoapPosts;

//We implemented
public class SoapPostsClient extends SoapClient implements Posts {

	SoapPosts impl;

	/*public SoapPostsClient() {
		this( Discovery.findUrisOf("???", 1)[0]); //TODO
	}*/
	
	
	public SoapPostsClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public Result<Post> getPost(String postId) {
		return super.tryCatchResult(() -> impl().getPost(postId));
	}
	
	//Singleton
	private SoapPosts impl()  {
		if( impl == null ) {
			synchronized(this) {
				if(impl == null) {
					QName QNAME = new QName(SoapPosts.NAMESPACE ,SoapPosts.NAME);
					Service service = Service.create(urlOrNull(uri),QNAME);
					impl = service.getPort(SoapPosts.class);
					super.setTimeout((BindingProvider) impl);
				}
			}
		}
		return impl;
	}


	@Override
	public Result<String> createPost(Post post) {
		return super.tryCatchResult(() -> impl().createPost(post));
	}


	@Override
	public Result<Void> deletePost(String postId) {
		return super.tryCatchVoid(() -> impl().deletePost(postId));
	}


	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		return super.tryCatchVoid(() -> impl().like(postId, userId, isLiked));
	}


	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		return super.tryCatchResult(() -> impl().isLiked(postId, userId));
	}


	@Override
	public Result<List<String>> getPosts(String userId) {
		return super.tryCatchResult(() -> impl().getPosts(userId));
	}


	@Override
	public Result<List<String>> getFeed(String userId) {
		return super.tryCatchResult(() -> impl().getFeed(userId));
	}

	@Override
	public Result<Void> removeAllPostsFromUser(String userId) {
		return super.tryCatchVoid(() -> impl().removeAllPostsFromUser(userId));
	}
}
