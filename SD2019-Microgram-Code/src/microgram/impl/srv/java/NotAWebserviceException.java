package microgram.impl.srv.java;

public class NotAWebserviceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String NOT_A_WEBSERVICE_DEFAULT_MESSAGE =
			"URI read is not a Webservice";
	
	public NotAWebserviceException () {
		super(NOT_A_WEBSERVICE_DEFAULT_MESSAGE);
	}
	
	public NotAWebserviceException (String message) {
		super(message);
	}

}
