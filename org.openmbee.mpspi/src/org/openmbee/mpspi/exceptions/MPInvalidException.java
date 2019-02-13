package org.openmbee.mpspi.exceptions;

public class MPInvalidException extends MPCancelException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4140949645858457041L;

	/**
	 * Generated serialVersionID
	 */

	public MPInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPInvalidException(String message) {
		super(message);
	}
}
