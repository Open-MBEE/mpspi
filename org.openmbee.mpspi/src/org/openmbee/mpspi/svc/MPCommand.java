package org.openmbee.mpspi.svc;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.util.MPUtil;

public abstract class MPCommand {
    protected final EObject target;

    public void addLockTarget(Collection<EObject> c) {
        c.add(target);
    }

    protected final EStructuralFeature feature;

    public abstract void execute();

    private MPCommand(EObject target, EStructuralFeature feature) {
        this.target = target;
        this.feature = feature;
    }

    public static class Set extends MPCommand {
        private final Object value;
        private final boolean force;

        public static void go(EObject t, EStructuralFeature f, Object v) {
            t.eSet(f, v);
        }

        public static void forcibly(EObject t, EStructuralFeature f, Object v) {
            if (f.isChangeable()) {
                t.eSet(f, v);
            } else {
                try{
                    f.setChangeable(true);
                    t.eSet(f, v);
                } finally {
                    f.setChangeable(false);
                }
            }
        }

        public void execute() {
            if (force) {
                forcibly(target, feature, value);
            } else {
                go(target, feature, value);
            }
        }

        public Set(EObject target, EStructuralFeature feature, Object value, boolean force) {
            super(target, feature);
            this.value = value;
            this.force = force;
        }
    }

    public static class Unset extends MPCommand {
        public static void go(EObject t, EStructuralFeature f) {
            t.eUnset(f);
        }

        public void execute() {
            go(target, feature);
        }

        public Unset(EObject target, EStructuralFeature feature) {
            super(target, feature);
        }
    }

    public static class Add extends MPCommand {
        private final Object value;
        private final int index;

        public static void go(EObject t, EStructuralFeature f, Object v, int i) {
            @SuppressWarnings("unchecked")
            EList<Object> valueList = (EList<Object>) t.eGet(f);
            if (i >= 0) {
                MPUtil.setToList(valueList, i, v);
            } else {
                valueList.add(v);
            }
        }

        public void execute() {
            go(target, feature, value, index);
        }

        public Add(EObject target, EStructuralFeature feature, Object value, int index) {
            super(target, feature);
            assert feature.isMany();
            this.value = value;
            this.index = index;
        }

        public Add(EObject target, EStructuralFeature feature, Object value) {
            this(target, feature, value, -1);
        }
    }

    public static class Remove extends MPCommand {
        private final Object value;

        public void addLockTarget(Collection<EObject> c) {
            if (value instanceof EObject) {
                c.add((EObject) value);
            }
            super.addLockTarget(c);
        }

        public static void go(EObject t, EStructuralFeature f, Object v) {
			@SuppressWarnings("unchecked")
			EList<Object> valueList = (EList<Object>) t.eGet(f);
			valueList.remove(v);
        }

        public void execute() {
            go(target, feature, value);
        }

        public Remove(EObject target, EStructuralFeature feature, Object value) {
            super(target, feature);
            assert feature.isMany();
            this.value = value;
        }
    }

    public static class RemoveByIdx extends MPCommand {
        private final int index;

        public void addLockTarget(Collection<EObject> c) {
            Object vl = target.eGet(feature);
            if (vl instanceof EList) {
                @SuppressWarnings("unchecked")
				EList<Object> valueList = (EList<Object>) vl;
                if (valueList.size() > index) {
                    Object o = valueList.get(index);
                    if (o instanceof EObject) {
                        EObject eObj = (EObject) o;
                        c.add(eObj);
                    }
                }
            }

            super.addLockTarget(c);
        }

        public static void go(EObject t, EStructuralFeature f, int index) {
			@SuppressWarnings("unchecked")
			EList<Object> valueList = (EList<Object>) t.eGet(f);
			valueList.remove(index);
        }

        public void execute() {
            go(target, feature, index);
        }

        public RemoveByIdx(EObject target, EStructuralFeature feature, int index) {
            super(target, feature);
            assert feature.isMany();
            this.index = index;
        }
    }

    public static class Delete extends MPCommand {
        private final Resource resource;

        public static void go(EObject t, Resource r) {
            r.getContents().remove(t);
        }

        public void execute() {
            go(target, resource);
        }

        public Delete(EObject target, Resource resource) {
            super(target, null);
            this.resource = resource;
        }
    }

}
