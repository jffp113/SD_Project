package microgram.impl.srv.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import discovery.DiscoveryConfiguration;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import discovery.Discovery;
import utils.IP;


public class PostsRestServer {
	private static Logger Log = Logger.getLogger(PostsRestServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}

	public static final String SERVICE = "Microgram-Posts";

	public static final int PORT = 7777;
	public static String SERVER_BASE_URI = "http://%s:%s/rest";

	public static void main(String[] args) throws URISyntaxException {

		for(int i = 0; i < args.length; i +=2){
			if(args[i].equals("-profiles")){
				System.out.println("profiles=" + args[i+1]);
				DiscoveryConfiguration.numberOfProfilesServers = Integer.parseInt(args[i+1]);
			}
			else if(args[i].equals("-posts")){
				System.out.println("psots=" + args[i+1]);
				DiscoveryConfiguration.numberOfPostsServers = Integer.parseInt(args[i+1]);
			}
		}

		System.out.println(IP.hostAddress());
		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		(new RestServiceExecuter()).execute(SERVICE,Log,new RestPostsResources(new URI(serverURI)),serverURI);
	}
}
