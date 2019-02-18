package org.openmbee.mpspi.svc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.exceptions.MPException;
import org.openmbee.mpspi.exceptions.MPIllegalStateException;
import org.openmbee.mpspi.exceptions.MPUnsupportedOperationException;
import org.openmbee.mpspi.modifier.MPModifier;

public abstract class MPBaseAdapter extends MPAbstractAdapter {
    protected Resource checkResource() throws MPIllegalStateException {
        Resource r = getResource();
        if (r == null) {
            throw new MPIllegalStateException("Resource has not been loaded");
        }
        return r;
    }

	@Override
	public List<EObject> getRoots() throws MPException {
        Resource r = checkResource();
        return r.getContents();
	}


    private Map<EStructuralFeature, MPModifier> mpModifierMap = new HashMap<EStructuralFeature, MPModifier>();

    public void registerMPModifier(MPModifier modifier) throws MPException {
        EModelElement c = modifier.getModificationCriteria();
        if (c instanceof EStructuralFeature) {
            EStructuralFeature f = (EStructuralFeature) c;
            mpModifierMap.put(f, modifier);
        } else {
            throw new MPUnsupportedOperationException("Unsupported criteria: " + c + " in " + modifier);
        }
    }

    private List<MPCommand> mpCommandLog;

    private boolean isTransactionEnabled() {
        return (mpCommandLog != null);
    }

    protected void setTransaction(boolean flag) {
        if (flag) {
            mpCommandLog = new ArrayList<MPCommand>();
        } else {
            mpCommandLog = null;
        }
    }

    protected Set<EObject> getLockTargets() {
        Set<EObject> ret = new HashSet<EObject>(mpCommandLog.size());
        for (MPCommand mc : mpCommandLog) {
            mc.addLockTarget(ret);
        }
        return ret;
    }

    protected void clearTransaction() {
        mpCommandLog.clear();
    }

    protected void commit() throws MPException {
        for (MPCommand mpc: mpCommandLog) {
            mpc.execute();
        }
    }

    public void doSet(EObject target, EStructuralFeature feature, Object value) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Set(target, feature, value, false));
        } else {
            super.doSet(target, feature, value);
        }
    }

    protected void doSetForcibly(EObject target, EStructuralFeature feature, Object value) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Set(target, feature, value, true));
        } else {
            super.doSet(target, feature, value);
        }
    }

    public void doUnset(EObject target, EStructuralFeature feature) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Unset(target, feature));
        } else {
            super.doUnset(target, feature);
        }
    }

    public void doAdd(EObject target, EStructuralFeature feature, Object value, int index) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Add(target, feature, value, index));
        } else {
            super.doAdd(target, feature, value, index);
        }
    }

    public void doAdd(EObject target, EStructuralFeature feature, Object value) {
        doAdd(target, feature, value, -1);
    }

    public void doRemove(EObject target, EStructuralFeature feature, Object value) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Remove(target, feature, value));
        } else {
            super.doRemove(target, feature, value);
        }
    }

    public void doRemoveByIdx(EObject target, EStructuralFeature feature, int index) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.RemoveByIdx(target, feature, index));
        } else {
            super.doRemoveByIdx(target, feature, index);
        }
    }

    public void doDelete(EObject target, Resource resource) {
        if (isTransactionEnabled()) {
            mpCommandLog.add(new MPCommand.Delete(target, resource));
        } else {
            super.doDelete(target, resource);
        }
    }
	
	@Override
	public Object get(EObject eObj, EStructuralFeature feature) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            return m.get(eObj, feature);
        } else {
            return eObj.eGet(feature);
        }
	}

	@Override
	public void add(EObject eObj, EStructuralFeature feature, Object value, int index) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            m.add(eObj, feature, value, index);
        } else {
            doAdd(eObj, feature, value, index);
        }
	}

    @Override
    protected TranslateInitializersResult translateInitializers(EObject value, Map<EStructuralFeature, Object> inits) throws MPException {
        List<EStructuralFeature> translated = null;

        for (EStructuralFeature f : inits.keySet()) {
            MPModifier m = mpModifierMap.get(f);
            if (m == null) continue;
            Object initValue = inits.get(f);
            EObject v = m.init(value, f, initValue);
            if (v != null) {
                if (translated == null) {
                    translated = new ArrayList<EStructuralFeature>(1);
                }
                value = v;
                translated.add(f);
            }
        }
        if (translated == null) return null;

        Map<EStructuralFeature, Object> newInits = new HashMap<EStructuralFeature, Object>(inits.size());
        for (Map.Entry<EStructuralFeature, Object> e : inits.entrySet()) {
            EStructuralFeature feature = e.getKey();
            if (translated.contains(feature)) continue;
            Object val = e.getValue();
            newInits.put(feature, val);
        }

        return new TranslateInitializersResult(value, newInits);
    }

	@Override
	public void remove(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            m.remove(eObj, feature, value);
        } else {
            doRemove(eObj, feature, value);
        }
	}

	@Override
	public void removeByIdx(EObject eObj, EStructuralFeature feature, int index) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            m.removeByIdx(eObj, feature, index);
        } else {
            doRemoveByIdx(eObj, feature, index);
        }
	}

	@Override
	public void set(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            m.set(eObj, feature, value);
        } else {
            doSet(eObj, feature, value);
        }
	}

	@Override
	public void unset(EObject eObj, EStructuralFeature feature) throws MPException {
        MPModifier m = mpModifierMap.get(feature);
        if (m != null) {
            m.unset(eObj, feature);
        } else {
            doUnset(eObj, feature);
        }
	}
}
