package microgram.impl.srv.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import discovery.DiscoveryConfiguration;
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
		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		(new RestServiceExecuter()).execute(SERVICE,Log,new RestProfilesResources(new URI(serverURI)),serverURI);
	}
}
