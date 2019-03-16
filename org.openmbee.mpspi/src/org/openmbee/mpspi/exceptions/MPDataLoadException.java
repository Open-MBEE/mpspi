package org.openmbee.mpspi.exceptions;

public class MPDataLoadException extends MPFailureException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6601807011984294895L;

	public MPDataLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPDataLoadException(String message) {
		super(message);
	}
}
