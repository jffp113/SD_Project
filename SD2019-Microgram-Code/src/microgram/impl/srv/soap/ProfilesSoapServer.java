package microgram.impl.srv.soap;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.HttpServer;

import discovery.Discovery;
import utils.IP;


@SuppressWarnings("restriction")
public class ProfilesSoapServer {
	private static Logger Log = Logger.getLogger(ProfilesSoapServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	public static final int PORT = 7777;
	public static final String SERVICE = "Microgram-Profiles";
	public static String SERVER_BASE_URI = "http://%s:%s/soap";
	
	private static String SOAP_BASE_PATH = "/soap";
	
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",PORT),0);

		Endpoint soapEndpoint = Endpoint.create(new PostsWebService());
		soapEndpoint.publish(server.createContext(SOAP_BASE_PATH));
		
		server.start();
		
		String ip = IP.hostAddress();
		Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE, ip + ":" + PORT));
		Discovery.announce(SERVICE, SERVER_BASE_URI);
	}
}
