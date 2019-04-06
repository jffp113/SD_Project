package microgram.impl.srv.soap;

import java.util.logging.Logger;

@SuppressWarnings("restriction")
public class PostsSoapServer {
	private static Logger Log = Logger.getLogger(PostsSoapServer.class.getName());

	public static final String SERVICE = "Microgram-Posts";
	
	public static void main(String[] args) throws Exception {
		SoapServiceExecuter exec = new SoapServiceExecuter();
		exec.execute(SERVICE,new PostsWebService(),Log);
	}
	
}   
             