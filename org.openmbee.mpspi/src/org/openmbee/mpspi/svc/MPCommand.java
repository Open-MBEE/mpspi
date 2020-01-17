package org.openmbee.mpspi.svc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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

	public abstract void undo();

	private MPCommand(EObject target, EStructuralFeature feature) {
		this.target = target;
		this.feature = feature;
	}

	public static class Set extends MPCommand {
		private final Object newValue;
		private final Object oldValue;
		private final boolean force;
		private boolean isFirstValue;

		public static void go(EObject t, EStructuralFeature f, Object v) {
			t.eSet(f, v);
		}

		public static void forcibly(EObject t, EStructuralFeature f, Object v) {
			if (f.isChangeable()) {
				t.eSet(f, v);
			} else {
				try {
					f.setChangeable(true);
					t.eSet(f, v);
				} finally {
					f.setChangeable(false);
				}
			}
		}

		public void execute() {
			/* check if first time setting the value */
			isFirstValue = target.eIsSet(feature);
			if (force) {
				forcibly(target, feature, newValue);
			} else {
				go(target, feature, newValue);
			}

		}

		public Set(EObject target, EStructuralFeature feature, Object newValue, Object oldValue, boolean force) {
			super(target, feature);
			this.newValue = newValue;
			this.oldValue = oldValue;
			this.force = force;
		}

		@Override
		public void undo() {
			if (isFirstValue) {
				target.eUnset(feature);
			} else {
				go(target, feature, oldValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static class Unset extends MPCommand {
		private final Object value;
		private List<MPCommand> removeCommand = new ArrayList<MPCommand>();

		public static void go(EObject t, EStructuralFeature f) {
			t.eUnset(f);
		}

		public void execute() {
			if (feature instanceof EReference) {
				EReference eRef = (EReference) feature;
				if (eRef.getEOpposite() != null) {
					EReference oppositeRef = eRef.getEOpposite();
					if (oppositeRef.isMany()) {
						if (value instanceof Collection<?>) {
							Collection<EObject> eObjects = (Collection<EObject>) value;
							for (EObject eObject : eObjects) {
								MPCommand.Remove remove = new Remove(eObject, oppositeRef, target);
								removeCommand.add(remove);
							}

						} else {
							MPCommand.Remove remove = new Remove((EObject) value, oppositeRef, target);
							removeCommand.add(remove);
						}
					} else {
						if (value instanceof EList<?>) {
							EList<EObject> eList = (EList<EObject>) value;
							for (EObject eObject : eList) {
								MPCommand.Remove remove = new Remove(target, feature, eObject);
								removeCommand.add(remove);
							}
						}
					}
				}
			}

			for (MPCommand mpCommand : removeCommand) {
				mpCommand.execute();
			}

			go(target, feature);
		}

		public Unset(EObject target, EStructuralFeature feature, Object value) {
			super(target, feature);
			this.value = value;
		}

		@Override
		public void undo() {
			if (value != null && !(value instanceof Collection)) {
				Set.go(target, feature, value);
			}

			if (removeCommand != null) {
				for (MPCommand mpCommand : removeCommand) {
					mpCommand.undo();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static class Add extends MPCommand {
		private final Object value;
		private final int index;

		public static void go(EObject t, EStructuralFeature f, Object v, int i) {
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

		@Override
		public void undo() {
			Remove.go(target, feature, value);
		}
	}

	@SuppressWarnings("unchecked")
	public static class Remove extends MPCommand {
		private final Object value;

		public void addLockTarget(Collection<EObject> c) {
			if (value instanceof EObject) {
				c.add((EObject) value);
			}
			super.addLockTarget(c);
		}

		public static void go(EObject t, EStructuralFeature f, Object v) {
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

		@Override
		public void undo() {
			Add.go(target, feature, value, -1);
		}
	}

	@SuppressWarnings("unchecked")
	public static class RemoveByIdx extends MPCommand {
		private final int index;

		public void addLockTarget(Collection<EObject> c) {
			Object vl = target.eGet(feature);
			if (vl instanceof EList) {
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

		@Override
		public void undo() {
			// TODO Auto-generated method stub

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

		@Override
		public void undo() {
			resource.getContents().add(target);
		}
	}

}
