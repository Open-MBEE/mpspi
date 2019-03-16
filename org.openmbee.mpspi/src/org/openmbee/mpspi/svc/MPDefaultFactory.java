package org.openmbee.mpspi.svc;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.MPConstants;
import org.openmbee.mpspi.MPFactory;

public class MPDefaultFactory implements MPFactory {
	private static final Target target = new Target() {
        @Override
        public int getPriority() {
            return MPConstants.PRIORITY_FALLBACK;
        }
		@Override
		public String getNsURIPattern() {
			// Cover everything
			return ".*";
		}
	};

	@Override
	public Target getTarget() {
		return target;
	}

    private MPAdapter create(MPAdapter parent) {
		if (parent instanceof MPDefaultAdapter) {
			return parent;
		}
		return new MPDefaultAdapter();
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
