package microgram.impl.srv.soap;

import com.sun.net.httpserver.HttpServer;
import discovery.Discovery;
import utils.IP;

import javax.xml.ws.Endpoint;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

class SoapServiceExecuter {

    public static final int PORT = 7777;
    public static String SERVER_BASE_URI = "http://%s:%s/soap";
    public static String SOAP_BASE_PATH = "/soap";

    public void execute(String service, SoapService impl, Logger logger) throws Exception {

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",PORT),0);

        Endpoint soapEndpoint = Endpoint.create(impl);
        soapEndpoint.publish(server.createContext(SOAP_BASE_PATH));

        server.start();

        String ip = IP.hostAddress();
        logger.info(String.format("%s Soap Server ready @ %s\n", service, ip + ":" + PORT));
        Discovery.announce(service, SERVER_BASE_URI);
    }
}
