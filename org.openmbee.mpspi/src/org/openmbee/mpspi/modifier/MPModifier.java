package org.openmbee.mpspi.modifier;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.openmbee.mpspi.exceptions.MPException;

public interface MPModifier {
    EModelElement getModificationCriteria();

    Object get(EObject eobj, EStructuralFeature feature) throws MPException;

    void set(EObject obj, EStructuralFeature feature, Object value) throws MPException;
    
	void unset(EObject eObj, EStructuralFeature feature) throws MPException;

	void add(EObject owner, EStructuralFeature feature, Object value, int index) throws MPException;
	
	void remove(EObject owner, EStructuralFeature feature, Object value) throws MPException;

	void removeByIdx(EObject owner, EStructuralFeature feature, int index) throws MPException;

    EObject init(EObject eobj, EStructuralFeature initFeature, Object initFeatureValue) throws MPException;
}

