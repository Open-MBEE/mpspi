package org.openmbee.mpspi.modifier;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.openmbee.mpspi.exceptions.MPException;
import org.openmbee.mpspi.exceptions.MPUnsupportedOperationException;
import org.openmbee.mpspi.svc.MPBaseAdapter;

public abstract class MPAbstractModifier implements MPModifier {
	@Override
    public void set(EObject eObj, EStructuralFeature feature, Object value, Object oldValue) throws MPException {
        throw new MPUnsupportedOperationException("set is not implemented: " + this);
    }
	
	@Override
    public void unset(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
        throw new MPUnsupportedOperationException("unset is not implemented: " + this);
    }

	@Override
	public void add(EObject eObj, EStructuralFeature feature, Object value, int index) throws MPException {
        throw new MPUnsupportedOperationException("add is not implemented: " + this);
    }
	
	@Override
	public void remove(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
        throw new MPUnsupportedOperationException("remove is not implemented: " + this);
    }

	@Override
	public void removeByIdx(EObject eObj, EStructuralFeature feature, int index) throws MPException {
        throw new MPUnsupportedOperationException("removeIdx is not implemented: " + this);
    }

	@Override
    public EObject init(EObject eObj, EStructuralFeature initFeature, Object initFeatureValue) throws MPException {
        throw new MPUnsupportedOperationException("init is not implemented: " + this);
    }

    /********************************************************************************
     * Model Manipulation Utilities
     ********************************************************************************/

    protected final MPBaseAdapter adapter;

    protected MPAbstractModifier(MPBaseAdapter adapter) throws MPException {
        this.adapter = adapter;
        adapter.registerMPModifier(this);
    }

    protected void doSet(EObject eObj, EStructuralFeature feature, Object value, Object oldValue) {
        adapter.doSet(eObj, feature, value, oldValue);
    }

    protected void doUnset(EObject eObj, EStructuralFeature feature, Object value) {
        adapter.doUnset(eObj, feature, value);
    }

    protected void doAdd(EObject eObj, EStructuralFeature feature, Object value, int index) {
        adapter.doAdd(eObj, feature, value, index);
    }

    protected void doAdd(EObject eObj, EStructuralFeature feature, Object value) {
        adapter.doAdd(eObj, feature, value);
    }

    protected void doRemove(EObject eObj, EStructuralFeature feature, Object value) {
        adapter.doRemove(eObj, feature, value);
    }

    protected void doRemoveByIdx(EObject eObj, EStructuralFeature feature, int index) {
        adapter.doRemoveByIdx(eObj, feature, index);
    }

    /********************************************************************************
     * Notification Modifier utility
     ********************************************************************************/

    protected static boolean addNotificationModifier(Notifier notifier,
                                                     MPNotificationModifier notificationModifier) {
        EList<Adapter> as = notifier.eAdapters();
        if (as.contains(notificationModifier)) return false;
        as.add(notificationModifier);
        return true;
    }

    protected static boolean addNotificationModifierOwner(EObject eObj,
                                                          MPNotificationModifier notificationModifier) {
        EObject owner = eObj.eContainer();
        if (owner == null) return false;
        return addNotificationModifier(owner, notificationModifier);
    }

}
