package swingbeanformbuilder.core.exception;

/**
 * Thrown when a critical exception happened during the form
 * building process. 
 * 
 * @author s-oualid
 */
public class SBFBException extends RuntimeException {

	public SBFBException() {
		super();
	}

	public SBFBException(String arg0) {
		super(arg0);
	}

	public SBFBException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SBFBException(Throwable arg0) {
		super(arg0);
	}

}
