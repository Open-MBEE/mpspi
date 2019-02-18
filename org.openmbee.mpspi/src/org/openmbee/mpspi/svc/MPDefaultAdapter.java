package org.openmbee.mpspi.svc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.openmbee.mpspi.exceptions.MPAccessException;
import org.openmbee.mpspi.exceptions.MPException;

public class MPDefaultAdapter extends MPBaseAdapter {
    private ResourceSet resourceSet;

	private Resource resource;

	@Override
	public void load(URI uri, Map<LoadOption, Object> option) throws MPException {
		this.resourceSet = new ResourceSetImpl();
		this.resource = resourceSet.getResource(uri, true);
		if (this.resource == null) {
			throw new MPAccessException("Failed to load Model: " + uri);
		}
	}

	@Override
	public ReloadResult reload() throws MPException {
        Resource r = checkResource();
        if (r.isLoaded()) {
            r.unload();
        }
        try {
            r.load(Collections.EMPTY_MAP);
        } catch (IOException e) {
            throw new MPAccessException("Failed to reload", e);
        }
        return ReloadResult.NEED_REACTIVATE;
	}

	@Override
	public UndoResult undo() throws MPException {
		// TODO Need to work
		return null;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public void save(Map<SaveOption, Object> option) throws MPException {
        Resource r = checkResource();
		try {
			r.save(null);
		} catch (IOException e) {
			throw new MPAccessException("Failed to save model file: " + resource.getURI().toString(), e);
		}
	}

	@Override
	public void unload() throws MPException {
        Resource r = checkResource();
        r.unload();
        this.resource = null;
	}

	@Override
	public List<EObject> getWithType(EClassifier classifier) throws MPException {
        checkResource();
		// TODO Need to work
		return null;
	}

	@Override
	public void delete(EObject eObj) throws MPException {
        // We cannot use EcoreUtil.delete(eObj, true)
        // because we need to get aware of our modification and transaction mechanism
        // with doRemove() and doUnset()
        deleteRecursively(eObj);
	}

    protected void deleteEObject(EObject eobj) throws MPException {
        Collection<EStructuralFeature.Setting> usages = EcoreUtil.UsageCrossReferencer.find(eobj, resourceSet);
        for (EStructuralFeature.Setting setting : usages) {
            EObject eoS = setting.getEObject();
            EStructuralFeature f = setting.getEStructuralFeature();
            if (f.isChangeable()) {
            	if (FeatureMapUtil.isMany(eoS, f)) {
            		doRemove(eoS, f, eobj);
            	} else {
            		doUnset(eoS, f);
            	}
            }
        }
        if (eobj instanceof InternalEObject) {
            InternalEObject ieo = (InternalEObject) eobj;
            EObject container = ieo.eInternalContainer();
            if (container != null) {
                EReference feature = eobj.eContainmentFeature();
                if (feature.isChangeable()) {
                	if (FeatureMapUtil.isMany(container, feature)) {
                		doRemove(container, feature, eobj);
                	} else {
                		doUnset(container, feature);
                	}
                }
            }

            Resource dr = ieo.eDirectResource();
            if (dr != null) {
                doDelete(eobj, dr);
            }
        }
    }
	
	private void deleteRecursively(EObject eObj) throws MPException {
		EClass cls = eObj.eClass();
		deleteContents(eObj, cls);
        deleteEObject(eObj);
	}
	
	private void deleteContents(EObject eObj, EClass cls) throws MPException {
		for (EReference ref : cls.getEAllContainments()) {
			Object o = eObj.eGet(ref);
			if (o != null) {
				if (ref.isMany()) {
					@SuppressWarnings("unchecked")
					EList<EObject> children = (EList<EObject>) o;
					if (children != null && !children.isEmpty()) {
						EObject[] childrenArray = children.toArray(new EObject[0]);
						for (EObject child : childrenArray) {
							deleteRecursively(child);
						}
					}
				} else {
					deleteRecursively((EObject) o);
				}
			}
		}
	}
}
