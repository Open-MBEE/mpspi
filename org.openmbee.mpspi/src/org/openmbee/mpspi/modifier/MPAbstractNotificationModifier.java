package org.openmbee.mpspi.modifier;

import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class MPAbstractNotificationModifier extends AdapterImpl implements MPNotificationModifier {
    private final EStructuralFeature actualFeature;
    public EStructuralFeature getActualFeature() {
        return actualFeature;
    }

    protected MPAbstractNotificationModifier(EStructuralFeature actualFeature) {
        this.actualFeature = actualFeature;
    }

    public boolean equals(Object o) {
        if (!(o instanceof MPAbstractNotificationModifier)) return false;
        MPAbstractNotificationModifier nm = (MPAbstractNotificationModifier) o;
        return actualFeature.equals(nm.getActualFeature());
    }

    public int hashCode() {
        return actualFeature.hashCode();
    }

    private static class MPModifiedNotification extends NotificationImpl {
        private final EObject target;
        private final EStructuralFeature virtualFeature;
        public Object getNotifier() { return target; }
        public Object getFeature() { return virtualFeature; }

        MPModifiedNotification(EObject target,
                               int eventType,
                               EStructuralFeature virtualFeature,
                               Object oldValue,
                               Object newValue,
                               int position) {
            super(eventType, oldValue, newValue, position);
            this.target = target;
            this.virtualFeature = virtualFeature;
        }
    }

    private void notify(Notifier target, Notification msg) {
        if (!target.eDeliver()) return;
        EList<Adapter> adapters = target.eAdapters();
        if (adapters == null) return;
        int size = adapters.size();
        for (int i = 0; i < size; i++) {
            Adapter a = adapters.get(i);
            if (a instanceof MPModifiedNotificationReceiver) {
                a.notifyChanged(msg);
            }
        }
    }

    protected void send(EObject target, EStructuralFeature virtualFeature, Object obj, boolean isAdded) {
        int eventType;
        Object newValue, oldValue;
        if (isAdded) {
            if (virtualFeature.isMany()) {
                eventType = Notification.ADD;
            } else {
                eventType = Notification.SET;
            }
            newValue = obj;
            oldValue = null;
        } else {
            if (virtualFeature.isMany()) {
                eventType = Notification.REMOVE;
            } else {
                eventType = Notification.UNSET;
            }
            oldValue = obj;
            newValue = null;
        }
        Notification msg = new MPModifiedNotification(target, eventType,
                                                      virtualFeature, oldValue, newValue,
                                                      Notification.NO_INDEX);
        notify(target, msg);
    }

    protected abstract void changed(Object obj, Notification msg, boolean isAdded);
    protected void changed(List<Object> objs, Notification msg, boolean isAdded) {
        for (Object obj : objs) {
            changed(obj, msg, isAdded);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public void notifyChanged(Notification msg) {
        if (!actualFeature.equals(msg.getFeature())) return;
        switch (msg.getEventType()) {
        case Notification.ADD:
            {
                Object add = msg.getNewValue();
                changed(add, msg, true);
                return;
            }

        case Notification.ADD_MANY:
            {
                List<Object> adds = (List<Object>) msg.getNewValue();
                if (adds.size() <= 0) return;
                changed(adds, msg, true);
                return;
            }

        case Notification.REMOVE:
            {
                Object remove = msg.getOldValue();
                changed(remove, msg, false);
                return;
            }

        case Notification.REMOVE_MANY:
            {
                List<Object> removes = (List<Object>) msg.getOldValue();
                if (removes.size() <= 0) return;
                changed(removes, msg, false);
                return;
            }
        case Notification.SET:
            {
                Object set = msg.getNewValue();
                if (set == null) {
                	set = msg.getOldValue();
                	changed(set, msg, false);
                } else {
                	changed(set, msg, true);
                }
                return;
            }
        case Notification.UNSET:
            {
                Object set = msg.getOldValue();
                changed(set, msg, false);
                return;
            }
        }
    }

}
