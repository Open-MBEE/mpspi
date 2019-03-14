package org.openmbee.mpspi.exceptions;

public class MPDataLoadException extends MPFailureException {

	public MPDataLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public MPDataLoadException(String message) {
		super(message);
	}
}
