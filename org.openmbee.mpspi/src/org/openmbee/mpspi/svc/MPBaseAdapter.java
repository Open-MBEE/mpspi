package org.openmbee.mpspi.svc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.exceptions.MPException;
import org.openmbee.mpspi.exceptions.MPRedoException;
import org.openmbee.mpspi.exceptions.MPUndoException;
import org.openmbee.mpspi.exceptions.MPUnsupportedOperationException;
import org.openmbee.mpspi.modifier.MPModifier;
import org.openmbee.mpspi.util.MPUtil;

public abstract class MPBaseAdapter extends MPAbstractAdapter {
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

	private Stack<Stack<List<MPCommand>>> undoLogs = new Stack<Stack<List<MPCommand>>>();

	/** it will clear after completing the whole transaction */
	private Stack<List<MPCommand>> undoCommandLog;

	private Collection<MPCommand> mpCommandLog;

	private boolean isTransactionEnabled() {
		return (mpCommandLog != null);
	}

	protected void setTransaction(boolean flag) {
		if (flag) {
			mpCommandLog =  new ArrayList<MPCommand>();
			undoCommandLog = new Stack<List<MPCommand>>();
		} else {
			mpCommandLog = null;
			undoCommandLog = null;
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
		if (mpCommandLog != null)
			mpCommandLog.clear();
	}

	/**
	 * This Function add the mpCommadLog to undo stack if it's not empty
	 **/
	protected void addUndoLog() {
		if (undoCommandLog != null && !mpCommandLog.isEmpty()) {
			List<MPCommand> commands = new ArrayList<MPCommand>();
			commands.addAll(mpCommandLog);
			undoCommandLog.add(commands);
		}
	}

	/**
	 * store the undo stack of transaction into final stack
	 */
	@Override
	public void storeTransaction() {
		if (undoCommandLog != null && !undoCommandLog.isEmpty()) {
			undoLogs.push(undoCommandLog);
			// don't clear assign to new stack
			undoCommandLog = new Stack<List<MPCommand>>();
		}
	}

	protected void clearUndoStack() {
		undoLogs.clear();
	}

	protected void commit() throws MPException {
		for (MPCommand mpc : mpCommandLog) {
			mpc.execute();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UndoResult undo() throws MPException {
		if (undoLogs.isEmpty())
			return UndoResult.EMPTY_STACK;

		try {
			Stack<List<MPCommand>> undoLog = undoLogs.pop();
			Object redoObject = undoLog.clone();
			while (!undoLog.isEmpty()) {
				List<MPCommand> undo = undoLog.pop();
				for (MPCommand mpCommand : undo) {
					mpCommand.undo();
				}
			}
			redoStack.add((Stack)redoObject);
			return UndoResult.DONE;
		} catch (Exception e) {
			// clear the undo stack and throw exception
			clearUndoStack();
			throw new MPUndoException("Unable to revert, Please reload the model without saving." , e);
		}
	}

	
	private Stack<Stack<List<MPCommand>>> redoStack = new Stack<Stack<List<MPCommand>>>();;
	
	@Override
	public RedoResult redo() throws MPException {
		if (redoStack.isEmpty()) 
			return RedoResult.EMPTY_STACK;
		try {
			Stack<List<MPCommand>> redoLog = redoStack.pop();
			while (!redoLog.isEmpty()) {
				List<MPCommand> redo = redoLog.pop();
				for (MPCommand mpCommand : redo) {
					mpCommand.execute();
				}
			}
			return RedoResult.DONE;
		} catch (Exception e) {
			// clear the redo stack and throw exception
			clearRedoStack();
			throw new MPRedoException("Unable to bring back, Please reload the model without saving." , e);
		}
	}
	
	private void clearRedoStack() {
		redoStack.clear();
	}
	
	
	
	public void doSet(EObject target, EStructuralFeature feature, Object value, Object oldValue) {
		if (isTransactionEnabled()) {
			mpCommandLog.add(new MPCommand.Set(target, feature, value, oldValue, false));
		} else {
			super.doSet(target, feature, value, oldValue);
		}
	}

	protected void doSetForcibly(EObject target, EStructuralFeature feature, Object value, Object oldValue) {
		if (isTransactionEnabled()) {
			mpCommandLog.add(new MPCommand.Set(target, feature, value, oldValue, true));
		} else {
			super.doSetForcibly(target, feature, value, oldValue);
		}
	}

	public void doUnset(EObject target, EStructuralFeature feature, Object value) {
		if (isTransactionEnabled()) {
			mpCommandLog.add(new MPCommand.Unset(target, feature, value));
		} else {
			super.doUnset(target, feature, value);
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
			if (MPUtil.isVirtual(feature))
				return;
			doAdd(eObj, feature, value, index);
		}
	}

	@Override
	protected TranslateInitializersResult translateInitializers(EObject value, Map<EStructuralFeature, Object> inits)
			throws MPException {
		List<EStructuralFeature> translated = null;

		for (EStructuralFeature f : inits.keySet()) {
			MPModifier m = mpModifierMap.get(f);
			if (m == null)
				continue;
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
		if (translated == null)
			return null;

		Map<EStructuralFeature, Object> newInits = new HashMap<EStructuralFeature, Object>(inits.size());
		for (Map.Entry<EStructuralFeature, Object> e : inits.entrySet()) {
			EStructuralFeature feature = e.getKey();
			if (translated.contains(feature))
				continue;
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
			if (MPUtil.isVirtual(feature))
				return;
			doRemove(eObj, feature, value);
		}
	}

	@Override
	public void removeByIdx(EObject eObj, EStructuralFeature feature, int index) throws MPException {
		MPModifier m = mpModifierMap.get(feature);
		if (m != null) {
			m.removeByIdx(eObj, feature, index);
		} else {
			if (MPUtil.isVirtual(feature))
				return;
			doRemoveByIdx(eObj, feature, index);
		}
	}

	@Override
	public void set(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
		MPModifier m = mpModifierMap.get(feature);
		if (m != null) {
            Object oldValue = get(eObj, feature);
			m.set(eObj, feature, value, oldValue);
		} else {
			if (MPUtil.isVirtual(feature))
				return;
            Object oldValue = get(eObj, feature);
			doSet(eObj, feature, value, oldValue);
		}
	}

	@Override
	public void unset(EObject eObj, EStructuralFeature feature, Object value) throws MPException {
		MPModifier m = mpModifierMap.get(feature);
		if (m != null) {
			m.unset(eObj, feature, value);
		} else {
			if (MPUtil.isVirtual(feature))
				return;
			doUnset(eObj, feature, value);
		}
	}
}
