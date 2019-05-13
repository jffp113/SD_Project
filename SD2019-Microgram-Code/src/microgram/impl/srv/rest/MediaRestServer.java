package microgram.impl.srv.rest;

import java.net.URI;
import java.util.logging.Logger;
import discovery.DiscoveryConfiguration;
import utils.IP;


public class MediaRestServer {
	private static Logger Log = Logger.getLogger(PostsRestServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}
	
	public static final int PORT = 9999;
	public static final String SERVICE = "Microgram-MediaStorage";
	public static String SERVER_BASE_URI = "https://%s:%s/rest";
	
	public static void main(String[] args) throws Exception {
		DiscoveryConfiguration.setArgs(args);
		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		(new RestServiceExecuter()).execute(SERVICE,Log,new RestPostsResources(new URI(serverURI)),serverURI);
	}

}