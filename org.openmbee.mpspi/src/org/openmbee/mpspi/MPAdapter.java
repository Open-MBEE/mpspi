package org.openmbee.mpspi;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.openmbee.mpspi.exceptions.MPException;

/**
 * MPAdapter defines the adapter interface for users to access EMF models.
 * 
 * @author himi
 */
public interface MPAdapter {
    public enum InformationOption {
        NAME, // String
        VERSION, // String
        SPECIFICATIONS, // Specification[]
    }

    public interface Specification extends Serializable {
        boolean canAskUsers();
        String getScheme();
        String getSuffix();
        String getDescription();
    }

    /**
     * Retrieve the information of the MPAdapter specified by option.
     * If no information available, it returns null.
     */
    Serializable getInformation(InformationOption option) throws MPException;

    public enum LoadOption {
        LOCAL,
        LOAD_OPTION1,
        LOAD_OPTION2,
        LOAD_OPTION3
    }

    /**
     * Load the model of uri.
     * 
     * @param uri
     * @param option
     * @throws MPException
     */
    void load(URI uri, Map<LoadOption, Object> option) throws MPException;

    Map<LoadOption, Object> DEFAULT_LOAD_OPTION = Collections.emptyMap();

    public enum ReloadResult {
    	UNSUPPORTED,
    	DONE,
    	NEED_REACTIVATE
    }

    /**
     * reload the model
     * @throws MPException
     */
    ReloadResult reload() throws MPException;

    public enum UndoResult {
    	UNSUPPORTED,
    	DONE,
    	ERROR,
    	NEED_REACTIVATE
    }

    /**
     * reload the last modification
     * @throws MPException
     */
    UndoResult undo() throws MPException;

    /**
     * Store the Transaction used to Undo 
     * */
     void storeTransaction() throws MPException;
    
    /**
     * Return the resource that has been loaded.
     *
     * @throws MPException
     */
    Resource getResource();

    /**
     * Return EPackage.Registry used by the adapter.
     * Service consumers need to use the returned registry to obtain Ecore.
     *
     * @throws MPException
     */
    EPackage.Registry getPackageRegistry();

    public enum SaveOption {
        LOCAL,
        COMMIT,
        BRANCH,
        SAVE_COPY_AS,
        SAVE_URI,
        SAVE_OPTION1,
        SAVE_OPTION2,
        SAVE_OPTION3
    }

    /**
     * Save the model.
     *
     * @throws MPException
     */
    void save(Map<SaveOption, Object> option) throws MPException;
    Map<SaveOption, Object> DEFAULT_SAVE_OPTION = Collections.emptyMap();

    void unload() throws MPException;

    List<EObject> getRoots(String name) throws MPException;

    List<EObject> getByEClass(EClass eCls) throws MPException;

    void delete(EObject eObj) throws MPException;

    Object get(EObject eObj, EStructuralFeature eStructuralFeature) throws MPException;

    EObject instantiate(EClass eClass, Map<EStructuralFeature, Object> inits) throws MPException;

    void add(EObject eObj, EStructuralFeature feature, Object value, int index) throws MPException;

    void remove(EObject eObj, EStructuralFeature feature, Object obj) throws MPException;

    void removeByIdx(EObject eObj, EStructuralFeature feature, int index) throws MPException;

    void set(EObject eObj, EStructuralFeature feature, Object value, Object oldValue) throws MPException;

    void unset(EObject eObj, EStructuralFeature feature, Object value) throws MPException;

    /*
    public interface Agent {
        String CAPABILITY = "Capability";
        Serializable get(String methodName) throws MPException;
        Serializable post(String methodName, Serializable... args) throws MPException;
    }

    void setAgent(Agent agent);
    */
}
