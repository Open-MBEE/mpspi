package org.openmbee.mpspi;

import org.openmbee.mpspi.discovery.MPAdapterRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.openmbee.mpspi"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

    private MPAdapterRegistry mpAdapterRegistry;

    public MPAdapterRegistry.Discovery newDiscovery() {
        return mpAdapterRegistry.newDiscovery();
    }

    public static void registerMPFactory(BundleContext context, MPFactory factory) {
        context.registerService(MPFactory.class, factory, null);
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Activator.plugin = this;
        registerMPFactory(context, new org.openmbee.mpspi.svc.MPDefaultFactory());
        this.mpAdapterRegistry = MPAdapterRegistry.newInstance(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Activator.plugin = null;
	}
}
