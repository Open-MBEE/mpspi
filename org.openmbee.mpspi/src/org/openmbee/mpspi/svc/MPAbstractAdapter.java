package org.openmbee.mpspi.svc;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.exceptions.MPException;
import org.openmbee.mpspi.exceptions.MPInstantiateException;
import org.openmbee.mpspi.util.MPUtil;

public abstract class MPAbstractAdapter implements MPAdapter {
    @Override
    public EPackage.Registry getPackageRegistry() {
        Resource r = getResource();
        ResourceSet rs = r.getResourceSet();
        EPackage.Registry pr = rs.getPackageRegistry();
        if (pr != null) return pr;
        return EPackage.Registry.INSTANCE;
    }

    private static URI checkSaveAs(Map<SaveOption, Object> option) {
        if (option == null) return null;
        Object o = option.get(SaveOption.SAVE_URI);
        if (o == null) return null;
        if (o instanceof URI) return (URI) o;
        throw new IllegalArgumentException("The value of SaveOption.SAVE_URI must be URI: " + o);
    }

    private static boolean checkSaveCopyAs(Map<SaveOption, Object> option) {
        if (option == null) return false;
        Object o = option.get(SaveOption.SAVE_COPY_AS);
        if (o == null) return false;
        if (o instanceof Boolean) return ((Boolean) o).booleanValue();
        throw new IllegalArgumentException("The value of SaveOption.SAVE_COPY_AS must be Boolean: " + o);
    }

    /**
     * Initialize `eObj' with `inits'.
     * That is, for each feature and value of 'inits', do eObj.eSet(feature, value) even if 
     * @param eObj
     * @param inits
     */
    protected void initializeFeatures(EObject eObj, Map<EStructuralFeature, Object> inits) throws MPException {
        for (Map.Entry<EStructuralFeature, Object> e : inits.entrySet()) {
            EStructuralFeature feature = e.getKey();
            if (MPUtil.isVirtual(feature)) continue;
            Object val = e.getValue();
            doSetForcibly(eObj, feature, val);
        }
    }

    protected static class TranslateInitializersResult {
        public final EObject eObject;
        public final Map<EStructuralFeature, Object> inits;

        public TranslateInitializersResult(EObject eObj, Map<EStructuralFeature, Object> inits) {
            this.eObject = eObj;
            this.inits = inits;
        }
    }
    protected TranslateInitializersResult translateInitializers(EObject eObj, Map<EStructuralFeature, Object> inits) throws MPException {
        return null;
    }

    @Override
    public EObject instantiate(EClass eClass, Map<EStructuralFeature, Object> inits) throws MPException {
		try {
            EPackage ePkg = eClass.getEPackage();
			EObject eObj = ePkg.getEFactoryInstance().create(eClass);
            TranslateInitializersResult tir = translateInitializers(eObj, inits);
            if (tir != null) {
                eObj = tir.eObject;
                inits = tir.inits;
            }
            initializeFeatures(eObj, inits);
            return eObj;
		} catch (IllegalArgumentException e) {
			throw new MPInstantiateException(eClass, e);
        }
    }

    /********************************************************************************
       Basic EMF operations:
       We strongly recommend to use these to modify EMF model elements in your MPAdapter
       because it can be extended to support transaction and locking if needed
    ********************************************************************************/

    public void doSet(EObject target, EStructuralFeature feature, Object value) {
        MPCommand.Set.go(target, feature, value);
    }

    protected void doSetForcibly(EObject target, EStructuralFeature feature, Object value) {
        MPCommand.Set.forcibly(target, feature, value);
    }

    public void doUnset(EObject target, EStructuralFeature feature) {
        MPCommand.Unset.go(target, feature);
    }

    public void doAdd(EObject target, EStructuralFeature feature, Object value, int index) {
        MPCommand.Add.go(target, feature, value, index);
    }

    public void doAdd(EObject target, EStructuralFeature feature, Object value) {
        doAdd(target, feature, value, -1);
    }

    public void doRemove(EObject target, EStructuralFeature feature, Object value) {
        MPCommand.Remove.go(target, feature, value);
    }

    public void doRemoveByIdx(EObject target, EStructuralFeature feature, int index) {
        MPCommand.RemoveByIdx.go(target, feature, index);
    }

    public void doDelete(EObject target, Resource resource) {
        MPCommand.Delete.go(target, resource);
    }
}
