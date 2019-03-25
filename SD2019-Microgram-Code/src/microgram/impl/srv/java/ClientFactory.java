package microgram.impl.srv.java;

import java.net.URI;

import discovery.Discovery;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.impl.clt.rest.RestMediaClient;
import microgram.impl.clt.rest.RestPostsClient;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.clt.soap.SoapPostsClient;
import microgram.impl.clt.soap.SoapProfilesClient;
import microgram.impl.srv.rest.MediaRestServer;
import microgram.impl.srv.rest.PostsRestServer;
import microgram.impl.srv.rest.ProfilesRestServer;

class ClientFactory {
	public static final int N_PROFILES = 1;
	public static final int N_MEDIA = 1;
	public static final int N_POSTS = 1;
	
	private static final String RESOURCE_SEPARATOR = "/";
	private static final String REST = "rest";
	private static final String SOAP = "soap";
	
	
	
	private static String resourceType(URI uri) {
		String uriStr = uri.toString();
		int lastBar = uriStr.lastIndexOf(RESOURCE_SEPARATOR);
		String resource = uriStr.substring(lastBar + 1);
		
		return resource;
	}
	
	private static Media buildAMedia(URI uri) {
		String resource = resourceType(uri);
		
		if(resource.equals(REST))
			return new RestMediaClient(uri);
		
		return null;
	}
	
	private static Posts buildAPost(URI uri) {
		String resource = resourceType(uri);
		
		if(resource.equals(REST))
			return new RestPostsClient(uri);
		else if(resource.equals(SOAP))
			return new SoapPostsClient(uri);
		
		return null;
	}
	
	private static Profiles buildAProfile(URI uri) {
		String resource = resourceType(uri);
		
		if(resource.equals(REST))
			return new RestProfilesClient(uri);
		else if(resource.equals(SOAP))
			return new SoapProfilesClient(uri);
		
		return null;
	}
	
	static Profiles[] buildProfile() {
		URI[] profileUris = Discovery.findUrisOf(ProfilesRestServer.SERVICE, N_PROFILES);
		
		if(profileUris.length == 0)
			return null;
		
		Profiles[] profiles = new Profiles[profileUris.length];
		
		for(int i = 0; i < profiles.length; i++) {
			profiles[i] = buildAProfile(profileUris[i]);
		}
		
		
		return profiles;
	}
		
	static Media[] buildMedia() {
		URI[] storageUris = Discovery.findUrisOf(MediaRestServer.SERVICE, N_MEDIA);
		if(storageUris.length == 0)
			return null;
		
		Media[] medias = new Media[storageUris.length];
		
		for (int i = 0; i < medias.length; i++)
			medias[i] = buildAMedia(storageUris[i]);
			
		return medias;
	}
	
	static Posts[] buildPosts() {
		URI[] postsUris = Discovery.findUrisOf(PostsRestServer.SERVICE, N_POSTS);
		if(postsUris.length == 0)
			return null;
			
		Posts[] posts = new Posts[postsUris.length];
		
		for (int i = 0; i < posts.length; i++)
			posts[i] = buildAPost(postsUris[i]);

		return posts;
	}
	
	
	public static void main(String[] args) {
		
	}
}
