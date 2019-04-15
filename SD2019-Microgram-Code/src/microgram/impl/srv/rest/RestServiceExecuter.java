package microgram.impl.srv.rest;

import discovery.Discovery;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.IP;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServiceExecuter {

    public static final int PORT = 7777;
    public static String SERVER_BASE_URI = "http://%s:%s/rest";

    public void execute(String SERVICE,Logger Log,RestResource resource ) {
        Log.setLevel( Level.FINER );

        String ip = IP.hostAddress();
        String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

        ResourceConfig config = new ResourceConfig();

        config.register(resource);

        JdkHttpServerFactory.createHttpServer( URI.create(serverURI.replace(ip, "0.0.0.0")), config);

        Log.info(String.format("%s Server ready @ %s\n",  SERVICE, serverURI));

        Discovery.announce(SERVICE, serverURI);
    }
}
