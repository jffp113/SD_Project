package microgram.impl.srv.rest;

import discovery.Discovery;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.IP;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServiceExecuter {


    public void execute(String SERVICE,Logger Log,RestResource resource, String serverURI) {
        Log.setLevel( Level.FINER );

        ResourceConfig config = new ResourceConfig();

        config.register(resource);

        JdkHttpServerFactory.createHttpServer( URI.create(serverURI.replace(IP.hostAddress(), "0.0.0.0")), config);

        Log.info(String.format("%s Server ready @ %s\n",  SERVICE, serverURI));

        Discovery.announce(SERVICE, serverURI);
        Log.info("Server Should be Annoucing");
    }
}
