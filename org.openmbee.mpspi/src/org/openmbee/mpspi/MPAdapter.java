package org.openmbee.mpspi;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.exceptions.MPException;

public interface MPAdapter {
    public enum LoadOption {
        LOCAL,
        LOAD_OPTION1,
        LOAD_OPTION2,
        LOAD_OPTION3
    }

    void load(URI uri, Map<LoadOption, Object> option) throws MPException;

    public enum ReloadResult {
    	UNSUPPORTED,
    	DONE,
    	NEED_REACTIVATE
    }

    /**
     * reload the resource.
     * @throws MPException
     * @throws IllegalStateException if another resource is already loaded
     */
    ReloadResult reload() throws MPException;

    public interface UndoResult {
        // TODO
    }
    UndoResult undo() throws MPException;

    Resource getResource();

    EPackage.Registry getPackageRegistry();

    public enum SaveOption {
        LOCAL,
        COMMIT,
        BRANCH,
        SAVE_COPY_AS,
        SAVE_URI,
        SAVE_OPTION1,
        SAVE_OPTION2,
        SAVE_OPTION3
    }

    void save(Map<SaveOption, Object> option) throws MPException;

    void unload() throws MPException;

    EObject getRootDataSource() throws MPException;

    List<EObject> getDataSource(EClassifier classifier) throws MPException;

    void delete(EObject eObj) throws MPException;

    Object get(EObject eObj, EStructuralFeature eStructuralFeature) throws MPException;

    EObject instantiate(EClass eClass, Map<EStructuralFeature, Object> inits) throws MPException;

    void add(EObject eObjOwner, EStructuralFeature feature, Object value, int index) throws MPException;

    void remove(EObject eObjOwner, EStructuralFeature feature, Object eObj) throws MPException;

    void removeByIdx(EObject eObjOwner, EStructuralFeature feature, int index) throws MPException;

    void set(EObject eObjOwner, EStructuralFeature feature, Object value) throws MPException;

    void unset(EObject eObjOwner, EStructuralFeature feature) throws MPException;
}
