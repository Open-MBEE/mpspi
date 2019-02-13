package org.openmbee.mpspi.exceptions;

import org.eclipse.emf.ecore.EClass;

public class MPInstantiateException extends MPCancelException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5578462786401763852L;
	/**
	 * Generated serialVersionID
	 */

    public final EClass eClass;

	public MPInstantiateException(EClass eClass, Throwable cause) {
		super("Failed to instantiate: " + eClass, cause);
        this.eClass = eClass;
	}
}
