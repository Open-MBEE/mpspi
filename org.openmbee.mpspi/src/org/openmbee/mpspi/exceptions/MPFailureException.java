package org.openmbee.mpspi.exceptions;

public abstract class MPFailureException extends MPException {
	/**
	 * Generated serialVersionID
	 */
	private static final long serialVersionUID = 863544180681750816L;

	public MPFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPFailureException(String message) {
		super(message);
	}
}
