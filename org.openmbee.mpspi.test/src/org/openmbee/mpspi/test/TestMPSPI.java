package org.openmbee.mpspi.test;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openmbee.mpspi.Activator;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.discovery.MPAdapterRegistry;

public class TestMPSPI {
	private static MPAdapterRegistry.Discovery discovery;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		discovery = Activator.getDefault().newDiscovery();
	}

	@Test
	public void test() throws Exception {
		MPAdapter adapter = discovery.newAdapter(null, null, null, null);
		assertNotNull(adapter);
	}

}
