package org.openmbee.mpspi.exceptions;

public abstract class MPException extends Exception {
	/**
	 * Generated serialVersionID
	 */
	private static final long serialVersionUID = -5876911043509466150L;

	public MPException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPException(String message) {
		super(message);
	}
}
