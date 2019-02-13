package org.openmbee.mpspi.exceptions;

public abstract class MPFatalException extends MPException {
	/**
	 * Generated serialVersionID
	 */
	private static final long serialVersionUID = -6244957887701163383L;

	public MPFatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPFatalException(String message) {
		super(message);
	}
}
