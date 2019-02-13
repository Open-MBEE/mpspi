package org.openmbee.mpspi.exceptions;

public class MPLockException extends MPCancelException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3673514456106460703L;

	/**
	 * Generated serialVersionID
	 */

	public MPLockException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPLockException(String message) {
		super(message);
	}
}
