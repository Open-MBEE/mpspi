package org.openmbee.mpspi.exceptions;

public abstract class MPCancelException extends MPException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3042805021602298023L;

	public MPCancelException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPCancelException(String message) {
		super(message);
	}
}
