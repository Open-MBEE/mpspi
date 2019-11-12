package org.openmbee.mpspi.exceptions;

public class MPUndoException extends MPFatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9119039104292063407L;

	public MPUndoException(String message) {
		super(message);
	}
	
	public MPUndoException(String message, Throwable cause) {
		super(message, cause);
	}	
}
