package org.openmbee.mpspi.exceptions;

public class MPDisconnectionException extends MPFailureException {
	/**
	 * Generated serialVersionID
	 */
	private static final long serialVersionUID = -8658319568288018318L;

	public MPDisconnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPDisconnectionException(String message) {
		super(message);
	}
}
