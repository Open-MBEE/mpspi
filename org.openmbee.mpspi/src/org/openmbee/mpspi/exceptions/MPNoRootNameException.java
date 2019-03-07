package org.openmbee.mpspi.exceptions;

public class MPNoRootNameException extends MPFatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8318419601554512905L;
	
	public final String rootName;

	public MPNoRootNameException(String rootName) {
		super("No Such root name:" + rootName);
		this.rootName = rootName;
	}
}
