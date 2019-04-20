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


public class ProfilesRestServer {
	private static Logger Log = Logger.getLogger(ProfilesRestServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}

	public static final String SERVICE = "Microgram-Profiles";
	public static final int PORT = 8888;
	public static String SERVER_BASE_URI = "http://%s:%s/rest";

	
	public static void main(String[] args) throws URISyntaxException {

		DiscoveryConfiguration.setArgs(args);

		System.out.println(IP.hostAddress());
		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		System.out.println(DiscoveryConfiguration.numberOfProfilesServers);
		(new RestServiceExecuter()).execute(SERVICE,Log,new RestProfilesResources(new URI(serverURI)),serverURI);
	}
}
