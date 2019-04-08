package microgram.impl.srv.java;

public class NoServersAvailableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static String NO_SERVERS_AVAILABLE_DEFAULT_MESSAGE =
			"No servers available.";
	
	public NoServersAvailableException () {
		super(NO_SERVERS_AVAILABLE_DEFAULT_MESSAGE);
	}
	
	public NoServersAvailableException (String message) {
		super(message);
	}

}
