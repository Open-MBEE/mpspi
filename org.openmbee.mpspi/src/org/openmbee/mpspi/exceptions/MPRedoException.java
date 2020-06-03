/**
 * 
 */
package org.openmbee.mpspi.exceptions;

/**
 * @author mzeshan
 *
 */
public class MPRedoException extends MPFatalException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6684692097946706048L;

	public MPRedoException(String message) {
		super(message);
	}
	
	public MPRedoException(String message, Throwable cause) {
		super(message, cause);
	}	
	
}
