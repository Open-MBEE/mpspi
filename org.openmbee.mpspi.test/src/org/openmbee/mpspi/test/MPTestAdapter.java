package org.openmbee.mpspi.test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.exceptions.MPException;

public class MPTestAdapter implements MPAdapter {
	@Override
	public void load(URI uri, Map<LoadOption, Object> option) throws MPException {
	}

	@Override
	public ReloadResult reload() throws MPException {
		return ReloadResult.UNSUPPORTED;
	}

	@Override
	public UndoResult undo() throws MPException {
		return null;
	}

	@Override
	public Resource getResource() {
		return null;
	}

	@Override
	public Registry getPackageRegistry() {
        return EPackage.Registry.INSTANCE;
	}

	@Override
	public void save(Map<SaveOption, Object> option) throws MPException {
		
	}

	@Override
	public void unload() throws MPException {
	}

	@Override
	public List<EObject> getRoots(String name) throws MPException {
		return null;
	}

	@Override
	public List<EObject> getByEClass(EClass eCls) throws MPException {
		return null;
	}

	@Override
	public void delete(EObject eObj) throws MPException {
	}

	@Override
	public Object get(EObject eObj, EStructuralFeature eStructuralFeature) throws MPException {
		return null;
	}

	@Override
	public EObject instantiate(EClass eClass, Map<EStructuralFeature, Object> inits) throws MPException {
		return null;
	}

	@Override
	public void add(EObject eObjOwner, EStructuralFeature feature, Object value, int index) throws MPException {
	}

	@Override
	public void remove(EObject eObjOwner, EStructuralFeature feature, Object eObj) throws MPException {
	}

	@Override
	public void removeByIdx(EObject eObjOwner, EStructuralFeature feature, int index) throws MPException {
	}

	@Override
	public void set(EObject eObj, EStructuralFeature feature, Object value, Object oldValue) throws MPException {
	}

	@Override
	public void unset(EObject eObjOwner, EStructuralFeature feature, Object value ) throws MPException {
	}

	@Override
	public Serializable getInformation(InformationOption option) throws MPException {
		return null;
	}

	@Override
	public void storeTransaction() throws MPException {
		
	}
}
