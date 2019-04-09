package discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. Clients listen for contacts, until they discovery the requested number
 * of service instances with the given service name.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	
	private static final int MAX_BUFFER_SIZE = 65536;
	
	private static final int DEAFAULT_MULTICAST_PORT = 2266;
	
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	
	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	/**
	 * Starts sending service announcements at regular intervals... 
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 * 
	 */
	public static void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", DISCOVERY_ADDR, serviceName, serviceURI));
		
		byte[] pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		new Thread(() -> {
			try (DatagramSocket ms = new DatagramSocket()) {
				for (;;) {
					ms.send(pkt);
					//System.out.write(pkt.getData(), arg1, arg2);
					Thread.sleep(DISCOVERY_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Performs discovery of instances of the service with the given name.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @param  minRepliesNeeded the required number of service replicas to find. 
	 * @return an array of URI with the service instances discovered. Returns an empty, 0-length, array if the service is not found within the alloted time.
	 * 
	 */
	public static URI[] findUrisOf(String serviceName, int minRepliesNeeded) {
		
		Set<URI> uniqueURI = null;
		
		try(MulticastSocket sock = new MulticastSocket(DEAFAULT_MULTICAST_PORT)) {
			sock.joinGroup(DISCOVERY_ADDR.getAddress());
			uniqueURI = (new findUrisClass(sock, serviceName, minRepliesNeeded)).getRequiredUris();
		} catch (IOException e) {
			System.err.println("Error opening socket");
			e.printStackTrace();
		} 
		
		return uniqueURI.toArray(new URI[uniqueURI.size()]);
	}
	
	/**
	 * Class that listens the server's announcements
	 *
	 */
	private static class findUrisClass {
		
		public static final String N_SERVER_FOUND = "Found %d different Servers\n";
		
		/**
		 * Stores the data received
		 */
		private final byte[] buffer = new byte[MAX_BUFFER_SIZE];
		
		/**
		 * Packet containing the URI 
		 */
		private final DatagramPacket receive = new DatagramPacket(buffer, MAX_BUFFER_SIZE);
		
		/**
		 * Socket from which the announcements will be received
		 */
		private MulticastSocket sock;
		
		/**
		 * The name of the service being discovered
		 */
		private String serviceName;
		
		/**
		 * The required number of service replicas to find
		 */
		private int minRepliesNeeded;
		
		/**
		 * Set containing the received URI
		 */
		private Set<URI> uniqueURI;
		
		/**
		 * Time left for the client to be waiting for an URI before acknowledging something's wrong
		 */
		private int waitTimeLeft = DISCOVERY_TIMEOUT;
		
		public findUrisClass (MulticastSocket sock, String serviceName, int minRepliesNeeded) {
			this.sock = sock;
			this.serviceName = serviceName;
			this.minRepliesNeeded = minRepliesNeeded;
			this.uniqueURI = new HashSet<URI>(minRepliesNeeded);
		}
		
		/**
		 * Parses the content received to check if it is the URI the client is expecting
		 * @return The URI received or null if the received data is not what was expected
		 */
		private URI parseRequest() {
			String strData = new String(this.buffer,0,receive.getLength());
			String[] strParsedURI = strData.split(DELIMITER);
			
			if (strParsedURI.length != 2 && !strParsedURI[0].equals(this.serviceName))
				return null;
			
			try {
				return new URI(strParsedURI[1]);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * @return An URI received
		 * @throws IOException No URI is received in the allowed time
		 */
		private URI getAnUri () throws IOException {
			URI uri = null;
			long currentTime;
			
			do {
				currentTime = System.currentTimeMillis();
				this.sock.setSoTimeout(this.waitTimeLeft);
				this.sock.receive(receive);
				uri = parseRequest();
				this.waitTimeLeft -= (int) (System.currentTimeMillis() - currentTime);
				if (this.waitTimeLeft <= 0) {
					throw new IOException();
				}
			} while (uri == null);
			return uri;
		}
		
		/**
		 * Listens to the server announcements and records the URI received
		 * Stops listening if the server hasn't communicated for too long
		 * @return The set containing the URI received
		 */
		public Set<URI> getRequiredUris () {
			
			URI uri;			
			try {
				while (this.minRepliesNeeded > this.uniqueURI.size()) {
						uri = getAnUri();
					if (!this.uniqueURI.contains(uri)) {
						this.uniqueURI.add(uri);
						this.waitTimeLeft = DISCOVERY_TIMEOUT;
					}
				}
			} catch (Exception e) {
				System.err.printf(N_SERVER_FOUND,uniqueURI.size());
				e.printStackTrace();
			}
			
			return this.uniqueURI;
		}
		
	}
}
