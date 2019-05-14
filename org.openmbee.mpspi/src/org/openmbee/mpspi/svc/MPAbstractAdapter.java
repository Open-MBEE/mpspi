package org.openmbee.mpspi.svc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.openmbee.mpspi.MPAdapter;
import org.openmbee.mpspi.MPConstants;
import org.openmbee.mpspi.exceptions.MPException;
import org.openmbee.mpspi.exceptions.MPIllegalStateException;
import org.openmbee.mpspi.exceptions.MPInstantiateException;
import org.openmbee.mpspi.exceptions.MPNoRootNameException;
import org.openmbee.mpspi.util.MPUtil;

public abstract class MPAbstractAdapter implements MPAdapter {
    protected Resource checkResource() throws MPIllegalStateException {
        Resource r = getResource();
        if (r == null) {
            throw new MPIllegalStateException("Resource has not been loaded");
        }
        return r;
    }

    protected static class SpecificationImpl implements Specification {
		private static final long serialVersionUID = 1L;
		
		private final boolean askUsers;
        private final String scheme;
        private final String suffix;
        private final String description;

        protected SpecificationImpl(boolean askUsers, String scheme, String suffix, String description) {
            this.askUsers = askUsers;
            this.scheme = scheme;
            this.suffix = suffix;
            this.description = description;
        }

		@Override
		public boolean canAskUsers() {
			return askUsers;
		}

		@Override
		public String getScheme() {
			return scheme;
		}

		@Override
		public String getSuffix() {
			return suffix;
		}

		@Override
		public String getDescription() {
			return description;
		}
    }

    private static String convertSpecificationString(String str) {
        str = str.trim();
        if (str.length() == 0) return null;
        return str;
    }

    public static Specification specification(String spec) throws MPException {
        String[] strs = spec.split(",");
        if (strs.length < 4) throw new ServiceConfigurationError("Spefication String is invalid:" + spec);
        return new SpecificationImpl(Boolean.parseBoolean(strs[0]),
                                     convertSpecificationString(strs[1]),
                                     convertSpecificationString(strs[2]),
                                     convertSpecificationString(strs[3]));
    }

    public static Specification[] specifications(String ... specs) throws MPException {
        Specification[] r = new Specification[specs.length];
        for (int i = 0; i < specs.length; i++) {
            r[i] = specification(specs[i]);
        }
        return r;
    }

    @Override
    public Serializable getInformation(InformationOption option) throws MPException {
        switch (option) {
        case NAME:
            return "MPSPI adapter";
        case VERSION:
            return org.openmbee.mpspi.Activator.getVersionString();
        default:
            return null;
        }
    }

	@Override
	public List<EObject> getRoots(String name) throws MPException {
        Resource r = checkResource();
		if (MPConstants.ROOT_NAME.equals(name)) {
			List<EObject> ret = new ArrayList<EObject>(1);
	        EList<EObject> contents = r.getContents();
			if (!contents.isEmpty()) {
                ret.add(contents.get(0));
	        }
			return ret;
		} else if (MPConstants.ROOTS_NAME.equals(name)) {
	        return r.getContents();
		}

		throw new MPNoRootNameException(name);
	}

	@Override
	public List<EObject> getByEClass(EClass eCls) throws MPException {
        Resource r = checkResource();
        List<EObject> results = new ArrayList<EObject>();
        
        Iterator<EObject> it = r.getAllContents();
        while (it.hasNext()) {
        	EObject eObj = it.next();
        	if (eCls.isInstance(eObj)) {
                results.add(eObj);
            }
        }
        
        return results;
    }

    @Override
    public EPackage.Registry getPackageRegistry() {
        Resource r = getResource();
        if (r != null) {
            ResourceSet rs = r.getResourceSet();
            EPackage.Registry pr = rs.getPackageRegistry();
            if (pr != null) return pr;
        }
        return EPackage.Registry.INSTANCE;
    }

    public static URI checkSaveAs(Map<SaveOption, Object> option) {
        if (option == null) return null;
        Object o = option.get(SaveOption.SAVE_URI);
        if (o == null) return null;
        if (o instanceof URI) return (URI) o;
        throw new IllegalArgumentException("The value of SaveOption.SAVE_URI must be URI: " + o);
    }

    public static boolean checkSaveCopyAs(Map<SaveOption, Object> option) {
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
