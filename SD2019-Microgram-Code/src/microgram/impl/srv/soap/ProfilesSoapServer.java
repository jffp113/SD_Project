package microgram.impl.srv.soap;

import java.util.logging.Logger;

@SuppressWarnings("restriction")
public class ProfilesSoapServer {
	private static Logger Log = Logger.getLogger(ProfilesSoapServer.class.getName());

	public static final String SERVICE = "Microgram-Profiles";

	public static void main(String[] args) throws Exception {
		SoapServiceExecuter exec = new SoapServiceExecuter();
		exec.execute(SERVICE,new ProfilesWebService(),Log);
	}
}
