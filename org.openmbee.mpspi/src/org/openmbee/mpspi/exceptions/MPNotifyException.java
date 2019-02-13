package org.openmbee.mpspi.exceptions;

public abstract class MPNotifyException extends MPException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7198584472138371451L;

	public MPNotifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPNotifyException(String message) {
		super(message);
	}
}
