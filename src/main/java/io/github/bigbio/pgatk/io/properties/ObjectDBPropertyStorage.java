package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.SpectrumProperty;
import io.github.bigbio.pgatk.io.objectdb.LongObject;
import io.github.bigbio.pgatk.io.objectdb.ObjectsDB;
import io.github.bigbio.pgatk.io.objectdb.WaitingHandler;

import java.util.*;

/**
 * This class interacts with the back-end database to manage identification
 * objects.
 *
 * Interacting with the back-end database might cause
 * InterruptedException. These exceptions are passed as runtime exceptions for
 * methods returning identification objects.
 *
 * @author ypriverol
 */
public class ObjectDBPropertyStorage extends LongObject {

    //The directory where the database stored.
    private String dbDirectory;

    //The database which will contain the objects.
    private final ObjectsDB objectsDB;

    /**
     * Default constructor
     * @param objectsDB The object database used to store the objects.
     */
    public ObjectDBPropertyStorage(ObjectsDB objectsDB) {
        this.objectsDB = objectsDB;
    }

    /**
     * Returns the objects database used in this class.
     * @return the objects database used in this class
     */
    public ObjectsDB getObjectsDB() {
        return objectsDB;
    }

     /**
     * Returns the number of clusters identifications.
     * @return the number of spectrum identifications
     */
    public int getPropertyStorageSize() {
        return objectsDB.getNumber(SpectrumProperty.class);
    }

    /**
     * Returns the number of objects of a given class
     * @param className the class name of a given class
     * @return the number of objects
     */
    public int getNumber(Class className) {
        return objectsDB.getNumber(className);
    }

    /**
     * Returns an iterator of all objects of a given class
     *
     * @param className the class name of a given class
     * @param filters filters for the class
     *
     * @return the iterator
     */
    public Iterator<?> getIterator(Class className, String filters) {
        return objectsDB.getObjectsIterator(className, filters);
    }

    /**
     * Returns the keys of the objects of the given class,
     *
     * @param className the class
     * @return the keys of the objects
     */
    public HashSet<Long> getClassObjects(Class className) {
        return objectsDB.getClassObjects(className);
    }

    /**
     * Loads all objects of the class in cache.
     *
     * @param className the class name
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     */
    public void loadObjects(Class className, WaitingHandler waitingHandler,
                            boolean displayProgress) {
        objectsDB.loadObjects(className, waitingHandler, displayProgress);
    }

    /**
     * Loads all objects of given keys in cache.
     *
     * @param keyList the list of keys of given objects
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * method should be displayed on the waiting handler
     *
     */
    public void loadObjects(ArrayList<Long> keyList, WaitingHandler waitingHandler) {
        objectsDB.loadObjects(keyList, waitingHandler);
    }

    /**
     * Returns an array of all objects of a given list of keys
     * @param longKey the hash key
     * @return the objects
     */
    public Object retrieveObject(long longKey) {
        return objectsDB.retrieveObject(longKey);
    }

    /**
     * Returns the spectrum match with the given key.
     *
     * @param key the key of the match
     *
     * @return the spectrum match with the given key
     */
    public SpectrumProperty getSpectrumProperty(long key) {
        return (SpectrumProperty) retrieveObject(key);

    }

    /**
     * Returns an array of all objects of a given list of keys
     *
     * @param keyList the key list
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @return list of objects
     */
    public ArrayList<Object> retrieveObjects(
            Collection<Long> keyList,
            WaitingHandler waitingHandler,
            boolean displayProgress
    ) {

        return objectsDB.retrieveObjects(keyList, waitingHandler, displayProgress);
    }

    /**
     * Returns an array of all objects of a given class
     *
     * @param className the class name
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @return list of objects
     */
    public ArrayList<Object> retrieveObjects(Class className, WaitingHandler waitingHandler,
                                             boolean displayProgress) {

        return objectsDB.retrieveObjects(className, waitingHandler, displayProgress);

    }

    /**
     * Adds an object into the database.
     *
     * @param key the key of the object
     * @param object the object
     */
    public void addObject(
            long key,
            Object object
    ) {

        objectsDB.insertObject(key, object);

    }

    /**
     * Adds a list of objects into the database.
     *
     * @param objects the object
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     */
    public void addObjects(HashMap<Long, Object> objects, WaitingHandler waitingHandler,
                           boolean displayProgress) {

        objectsDB.insertObjects(objects, waitingHandler);

    }

    /**
     * Removes an object from the database.
     * @param key the key of the object
     */
    public void removeObject(long key) {
        Object object = objectsDB.retrieveObject(key);
        objectsDB.removeObject(key);
    }

    /**
     * Checks if database contains a certain object.
     * @param key the key of the object
     * @return true if database contains a certain object otherwise false
     */
    public boolean contains(long key) { return objectsDB.inDB(key); }

    /**
     * Remove a list of objects from the database.
     * @param keys the list of object keys
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     */
    public void removeObjects(ArrayList<Long> keys,
                              WaitingHandler waitingHandler,
                              boolean displayProgress) {
        objectsDB.removeObjects(keys, waitingHandler, displayProgress);
    }

    /**
     * Returns the database directory.
     * @return the database directory
     */
    public String getDatabaseDirectory() {
        return dbDirectory;
    }

    /**
     * Adds a {@link SpectrumProperty}. If an exception occurs when saving to the db it is
     * thrown as runtime exception.
     *
     * @param key the peptide match key
     * @param property the peptide match
     */
    public void addSpectrumProperty(long key, SpectrumProperty property) {
        objectsDB.insertObject(key, property);
    }

    /**
     * Adds a {@link SpectrumProperty}. If an exception occurs when saving to the db it is
     * thrown as runtime exception.
     *
     * @param key the peptide match key
     * @param property the peptide match
     */
    public void addSpectrumProperty(String key, SpectrumProperty property) {
        objectsDB.insertObject(asLongHash(key), property);
    }

    /**
     * Add a set of {@link SpectrumProperty}
     * @param properties the peptide matches in a map
     */
    public void addProperty(Map<Long, Object> properties ) {
        objectsDB.insertObjects(properties, null);
    }

    /**
     * Closes the database connection.
     */
    public void close() { objectsDB.close(); }

    /**
     * Indicates whether the connection to the DB is active.
     * @return true if the connection to the DB is active
     */
    public boolean isConnectionActive() {
        return ObjectsDB.isConnectionActive();
    }

    public void flush() {
        objectsDB.dumpToDB();
    }
}
