package org.openmbee.mpspi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
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

    private MPAdapter openDefault() throws Exception {
		URI uri = URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore");
		MPAdapter adapter = discovery.newAdapter(null, EcorePackage.eNS_URI, uri, null);
		assertNotNull(adapter);
		adapter.load(uri, null);
        return adapter;
    }

    @Test
    public void testMPTest() throws Exception {
		URI uri = URI.createURI("platform:/plugin/org.openmbee.mpspi.test/model/simple.mpspi.test");
		MPAdapter adapter = discovery.newAdapter(null, "http://www.openmbee.org/mpspi/test", uri, null);
        assertTrue(adapter instanceof MPTestAdapter);
    }

	@Test
	public void testOpenEcore() throws Exception {
        openDefault();
	}

	@Test
    public void testGet() throws Exception {
        MPAdapter adapter = openDefault();
        EObject root = adapter.getRoots().get(0);
        assertTrue(root instanceof EPackage);
        String name = (String) adapter.get(root, EcorePackage.eINSTANCE.getENamedElement_Name());
        assertEquals(name, "ecore");
    }

    

}
