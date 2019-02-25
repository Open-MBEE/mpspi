package org.openmbee.mpspi.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.MPConstants;
import org.openmbee.mpspi.MPFactory;

public class MPTestFactory implements MPFactory {
	private static final Target target = new Target() {
		@Override
		public String getNsURIPattern() {
			// Cover everything
			return ".*";
		}

		public String getModelURIPattern() {
			return ".*mpspi\\.test$";
		}

        public int getPriority() {
            return MPConstants.PRIORITY_HIGH + 1000;
        }
	};

	@Override
	public Target getTarget() {
		return target;
	}

    private MPAdapter create(MPAdapter parent) {
		if (parent instanceof MPTestAdapter) {
			return parent;
		}
		return new MPTestAdapter();
    }

	@Override
	public MPAdapter create(String nsURI, URI modelURI, MPAdapter parent) {
        return create(parent);
	}

	@Override
	public MPAdapter create(EPackage epkg, URI modelURI, MPAdapter parent) {
		return create(parent);
	}
}
