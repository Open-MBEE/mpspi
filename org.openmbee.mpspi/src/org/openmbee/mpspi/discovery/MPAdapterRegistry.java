package org.openmbee.mpspi.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.MPConstants;
import org.openmbee.mpspi.MPFactory;
import org.openmbee.mpspi.exceptions.MPException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;


public class MPAdapterRegistry implements ServiceListener {
    private final BundleContext context;

    private List<MPFactory> mpFactories = new ArrayList<MPFactory>();

    private static final String SERVICE_LISTENER_FILTER = "(" + Constants.OBJECTCLASS + "=" + MPFactory.class.getName() + ")";

    private void initialize() {
        try {
            Collection<ServiceReference<MPFactory>> srs = context.getServiceReferences(MPFactory.class, null);
            for (ServiceReference<MPFactory> sr : srs) {
                MPFactory mpf = context.getService(sr);
                mpFactories.add(mpf);
            }
        } catch (InvalidSyntaxException e) {
            // TODO Logging
            e.printStackTrace();
        }
        try {
            context.addServiceListener(this, SERVICE_LISTENER_FILTER);
        } catch (InvalidSyntaxException e) {
            // TODO Logging
            e.printStackTrace();
        }
    }

    public static MPAdapterRegistry newInstance(BundleContext context) {
        MPAdapterRegistry d = new MPAdapterRegistry(context);
        d.initialize();
        return d;
    }

    private MPAdapterRegistry(BundleContext context) {
        this.context = context;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference<?> sr = event.getServiceReference();
        Object o = context.getService(sr);
        if (!(o instanceof MPFactory)) return;
        MPFactory mpf = (MPFactory) o;
        switch (event.getType()) {
        case ServiceEvent.REGISTERED:
            mpFactories.add(mpf);
            break;
        case ServiceEvent.UNREGISTERING:
            mpFactories.remove(mpf);
            break;
        }
    }

    private static String uriToStr(URI uri) {
        if (uri == null) return null;
        return uri.trimQuery().toString();
    }

    private MPFactory search(String nsURIStr, URI modelURI) {
        int priority = MPConstants.PRIORITY_FALLBACK - 1;
        MPFactory factory = null;
        String modelURIStr = uriToStr(modelURI);

        for (MPFactory f: mpFactories) {
            MPFactory.Target target = f.getTarget();
            int p = target.getPriority();
            if (factory != null) {
                if (p <= priority) continue;
            }

            String nsURIPattern = target.getNsURIPattern();
            if ((nsURIStr != null) && (nsURIPattern != null)) {
                if (!nsURIStr.matches(nsURIPattern)) continue;
            }
            String modelURIPattern = target.getModelURIPattern();
            if ((modelURIStr != null) && (modelURIPattern != null)) {
                if (!modelURIStr.matches(modelURIPattern)) continue;
            }
            factory = f;
            priority = p;
        }
        return factory;
    }


    /********************************************************************************
     * Discovery Service
     ********************************************************************************/

    public class Discovery {
        private Map<EPackage, MPAdapter> adapterMap = new HashMap<EPackage, MPAdapter>();

        private EPackage toEPackage(EObject target) {
            if (target == null) return null;
            EClass eCls = target.eClass();
            if (eCls == null) return null;
            return eCls.getEPackage();
        }

        public MPAdapter newAdapter(EPackage ePkg, String nsURI, URI modelURI, MPAdapter parent) throws MPException {
            MPFactory factory = search(nsURI, modelURI);
            if (factory == null) return null;
            MPAdapter a = factory.create(nsURI, modelURI, parent);
            if (a == null) return null;
            adapterMap.put(ePkg, a);
            return a;
        }

        public MPAdapter newAdapter(EObject target, String nsURI, URI modelURI) throws MPException {
            EPackage ePkg = toEPackage(target);
            MPAdapter a = lookup(ePkg);
            return newAdapter(ePkg, nsURI, modelURI, a);
        }

        public MPAdapter newAdapter(EPackage ePkg, URI modelURI) throws MPException {
            MPAdapter a = lookup(ePkg);
            return newAdapter(ePkg, ePkg.getNsURI(), modelURI, a);
        }

        public MPAdapter lookup(EObject target) {
            EPackage ePkg = toEPackage(target);
            return lookup(ePkg);
        }

        public MPAdapter lookup(EPackage ePkg) {
            MPAdapter a = null;
            while (ePkg != null) {
                a = adapterMap.get(ePkg);
                if (a != null) return a;
                ePkg = ePkg.getESuperPackage();
            }
            
            return adapterMap.get(null);
        }
    }

    public Discovery newDiscovery() {
        return new Discovery();
    }

}
