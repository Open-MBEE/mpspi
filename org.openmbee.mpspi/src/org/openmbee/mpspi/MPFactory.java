package org.openmbee.mpspi;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.openmbee.mpspi.exceptions.MPException;

public interface MPFactory {
    public abstract static class Target {

        public String getModelURIPattern() {
            return null;
        }

        public abstract String getNsURIPattern();

        public int getPriority() {
            return MPConstants.PRIORITY_STANDARD;
        }
    }

    Target getTarget();

	/**
	 * 
	 * @param editingDomain
	 * @param nsURI
	 * @param modelURI
	 * @param parent
	 * @return
	 */
    MPAdapter create(String nsURI, URI modelURI, MPAdapter parent) throws MPException;
    
    /**
     * 
     * @param editingDomain
     * @param epkg
     * @param modelURI
     * @param parent
     * @return
     */
    MPAdapter create(EPackage epkg, URI modelURI, MPAdapter parent) throws MPException;
}
