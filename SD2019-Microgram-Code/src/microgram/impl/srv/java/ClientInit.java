package microgram.impl.srv.java;

import java.net.URI;

import discovery.Discovery;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.impl.clt.rest.RestMediaClient;
import microgram.impl.clt.rest.RestPostsClient;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.srv.rest.MediaRestServer;
import microgram.impl.srv.rest.PostsRestServer;
import microgram.impl.srv.rest.ProfilesRestServer;

class ClientInit {
	
	static Profiles profileInit() {
		URI[] profileUris = Discovery.findUrisOf(ProfilesRestServer.SERVICE, 1);
		if(profileUris.length == 0)
			return null;
			
		return new RestProfilesClient(profileUris[0]);
	}
		
	static Media storageInit() {
		URI[] storageUris = Discovery.findUrisOf(MediaRestServer.SERVICE, 1);
		if(storageUris.length == 0)
			return null;
			
		return new RestMediaClient(storageUris[0]);
	}
	
	
	static Posts postsInit() {
		URI[] postsUris = Discovery.findUrisOf(PostsRestServer.SERVICE, 1);
		if(postsUris.length == 0)
			return null;
			
		return new RestPostsClient(postsUris[0]);
	}
}
