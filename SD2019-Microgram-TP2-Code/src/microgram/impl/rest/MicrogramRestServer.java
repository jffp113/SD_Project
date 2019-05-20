package microgram.impl.rest;

import static utils.Log.Log;

import java.net.URI;
import java.util.logging.Level;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import discovery.Discovery;
import microgram.api.rest.RestPosts;
import microgram.api.rest.RestProfiles;
import microgram.impl.rest.posts.replicated.ReplicatedPostsResources;
import microgram.impl.rest.profiles.replicated.ReplicatedProfilesResources;
import utils.Args;
import utils.IP;

public class MicrogramRestServer {
	public static final int PORT = 18888;
	private static final String POSTS_SERVICE = "Microgram-Posts";
	private static final String PROFILES_SERVICE = "Microgram-Profiles";
	
	public static String SERVER_BASE_URI = "http://%s:%s/rest";

	public static void main(String[] args) throws Exception {
		Args.use(args);

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "OFF");
		System.setProperty("java.net.preferIPv4Stack", "true");

		Log.setLevel(Level.INFO);

		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

		Discovery.announce(POSTS_SERVICE, serverURI + RestPosts.PATH);
		Discovery.announce(PROFILES_SERVICE, serverURI + RestProfiles.PATH);
	
		ResourceConfig config = new ResourceConfig();

		config.register(new ReplicatedPostsResources());
		config.register(new ReplicatedProfilesResources());
		
//		config.register(new PrematchingRequestFilter());
//		config.register(new GenericExceptionMapper());

		JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, "0.0.0.0")), config);

		Log.fine(String.format("Posts+Profiles Combined Rest Server ready @ %s\n", serverURI));
	}

}
