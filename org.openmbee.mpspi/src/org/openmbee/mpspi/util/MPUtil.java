package org.openmbee.mpspi.util;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.openmbee.mpspi.MPConstants;

public class MPUtil {
    /**
     * Utility class to set an element to the list at the specified index. 
     * 
     * @param list  List to be added
     * @param index position at which element is set.
     * @param element It is to be set in list
     */
    public static <T> void setToList(java.util.List<T> list, int index, T element) {
    	int size = list.size();
    	if (size > index) {
    		list.set(index, element);
    	} else {
    		for (; size < index; size++) {
    			list.add(null);
    		}
    		list.add(element);
    	}    	
    }

    public static boolean isVirtual(EModelElement e) {
        EAnnotation a = e.getEAnnotation(MPConstants.ANNOTATION_VIRTUAL_ELEMENT);
        return (a != null);
    }
}
