package microgram.impl.srv.rest;

import java.net.URI;

import javax.net.ssl.SSLContext;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import discovery.Discovery;
import utils.IP;


public class MediaRestServer {

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}
	
	public static final int PORT = 9999;
	public static final String SERVICE = "Microgram-MediaStorage";
	public static String SERVER_BASE_URI = "https://%s:%s/rest";
	
	public static void main(String[] args) throws Exception {

		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		
		ResourceConfig config = new ResourceConfig();

		config.register(new RestMediaResources(new URI(serverURI).toString()));
		
		JdkHttpServerFactory.createHttpServer( URI.create(serverURI.replace(ip, "0.0.0.0")), config, SSLContext.getDefault());
		
		Discovery.announce(SERVICE, serverURI);
	}
}