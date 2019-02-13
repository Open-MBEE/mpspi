package org.openmbee.mpspi.exceptions;

public abstract class MPAdapterException extends MPCancelException {
	/**
	 * generated serial version ID
	 */
	private static final long serialVersionUID = 5898389111147588625L;

	public MPAdapterException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPAdapterException(String message) {
		super(message);
	}
}
