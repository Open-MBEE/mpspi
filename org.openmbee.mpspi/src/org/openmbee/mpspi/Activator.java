package org.openmbee.mpspi;

import org.openmbee.mpspi.discovery.MPAdapterRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class Activator implements BundleActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.openmbee.mpspi"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private Bundle bundle;

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

    public static String getVersionString() {
        Activator a = getDefault();
        if (a == null) return null;
        Bundle b = a.bundle;
        if (b == null) return null;
        Version v = b.getVersion();
        if (v == null) return null;
        return v.toString();
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Activator.plugin = this;
        this.bundle = context.getBundle();
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
