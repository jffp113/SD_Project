package microgram.impl.srv.java;

import java.net.URI;
import java.util.Arrays;

import discovery.Discovery;
import microgram.api.java.Media;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.impl.clt.java.RetryMediaClient;
import microgram.impl.clt.java.RetryPostsClient;
import microgram.impl.clt.java.RetryProfilesClient;
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
	
	private static final int NO_ERRORS = 0;

	private static final String REST = "rest";
	private static final String SOAP = "soap";
	
	private static Media buildAMedia(URI uri) throws NotAWebserviceException {
		String uriStr = uri.toString();		
		if(uriStr.endsWith(REST))
			return new RetryMediaClient(new RestMediaClient(uri));
		throw new NotAWebserviceException();
	}
	
	private static Posts buildAPost(URI uri) throws NotAWebserviceException {
		String uriStr = uri.toString();
		Posts result = null;
		
		if(uriStr.endsWith(REST))
			result = new RestPostsClient(uri);
		else if(uriStr.endsWith(SOAP))
			result = new SoapPostsClient(uri);
		else
			throw new NotAWebserviceException();
		
		return new RetryPostsClient(result);
	}
	
	private static Profiles buildAProfile(URI uri) throws NotAWebserviceException {
		String uriStr = uri.toString();
		Profiles result = null;
		
		if(uriStr.endsWith(REST))
			result = new RestProfilesClient(uri);
		else if(uriStr.endsWith(SOAP))
			result = new SoapProfilesClient(uri);
		else
			throw new NotAWebserviceException();		
		
		return new RetryProfilesClient(result);
	}
	
	private static URI[] discoverURI (String service, int number) throws NoServersAvailableException {
		URI[] uris = Discovery.findUrisOf(service, number);
		if(uris.length == 0)
			throw new NoServersAvailableException();
		return uris;
	}
	
	static Profiles[] buildProfile() throws NoServersAvailableException {
		URI[] profileUris = discoverURI(ProfilesRestServer.SERVICE, N_PROFILES);		
		Profiles[] profiles = new Profiles[profileUris.length];
		
		/*
		 * The number of URI's received that were neither REST nor SOAP
		 */
		int errorsFound = NO_ERRORS;
		for(int i = 0; i < profiles.length; i++) {
			try {
				profiles[i - errorsFound] = buildAProfile(profileUris[i]);
			} catch (NotAWebserviceException e) {
				errorsFound++;
			}
		}
		
		return Arrays.copyOf(profiles, profiles.length - errorsFound);
	}
		
	static Media[] buildMedia() throws NoServersAvailableException {
		URI[] storageUris = discoverURI(MediaRestServer.SERVICE, N_MEDIA);		
		Media[] medias = new Media[storageUris.length];
		
		int errorsFound = NO_ERRORS;
		for (int i = 0; i < medias.length; i++)
			try {
				medias[i - errorsFound] = buildAMedia(storageUris[i]);
			} catch (NotAWebserviceException e) {
				errorsFound++;
			}
			
		return Arrays.copyOf(medias, medias.length - errorsFound);
	}
	
	static Posts[] buildPosts() throws NoServersAvailableException {
		URI[] postsUris = discoverURI(PostsRestServer.SERVICE, N_POSTS);	
		Posts[] posts = new Posts[postsUris.length];
		
		int errorsFound = NO_ERRORS;
		for (int i = 0; i < posts.length; i++)
			try {
				posts[i - errorsFound] = buildAPost(postsUris[i]);
			} catch (NotAWebserviceException e) {
				errorsFound++;
			}

		return Arrays.copyOf(posts, posts.length - errorsFound);
	}
	
}
