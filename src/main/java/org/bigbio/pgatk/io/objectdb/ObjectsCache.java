package org.bigbio.pgatk.io.objectdb;


import static org.bigbio.pgatk.io.objectdb.DbMutex.loadObjectMutex;
import org.bigbio.pgatk.io.objectdb.WaitingHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.jdo.PersistenceManager;

/**
 * An object cache can be combined to an ObjectsDB to improve its performance. A
 * single cache can be used by different databases. This ought not to be
 * serialized. The length of lists/maps in the cache shall stay independent from
 * the number of objects in cache.
 *
 * @author Marc Vaudel
 * @author Dominik Kopczynski
 * @author Harald Barsnes
 */
public class ObjectsCache {

    /**
     * Empty default constructor
     */
    public ObjectsCache() {
    }

    /**
     * Share of the memory to be used.
     */
    private double memoryShare = 0.75;
    /**
     * Map of the loaded matches. db &gt; table &gt; object key &gt; object.
     */
    private final HashMap<Long, Object> loadedObjects = new HashMap<>();
    /**
     * Linked list to manage a queue for old entries.
     */
    private final LinkedList<Long> objectQueue = new LinkedList<>();
    /**
     * Indicates whether the cache is read only.
     */
    private boolean readOnly = false;
    /**
     * Reference to the objects DB.
     */
    private ObjectsDB objectsDB = null;
    /**
     * Number of objects that should at least be kept.
     */
    private final int keepObjectsThreshold = 10000;
    /**
     * If number number of registered objects exceeds value, commit to db should
     * be triggered.
     */
    private final int numToCommit = 10000;

    /**
     * Constructor.
     *
     * @param objectsDB the object database
     */
    public ObjectsCache(ObjectsDB objectsDB) {
        this.objectsDB = objectsDB;
    }

    /**
     * Returns the cache size in number of objects.
     *
     * @return the cache size in number of objects
     */
    public int getCacheSize() {
        return loadedObjects.size();
    }

    /**
     * Returns the share of heap size which can be used before emptying the
     * cache. 0.75 (default) means that objects will be removed from the cache
     * as long as more than 75% of the heap size is used.
     *
     * @return the share of heap size which can be used before emptying the
     * cache
     */
    public double getMemoryShare() {
        return memoryShare;
    }

    /**
     * Sets the share of heap size which can be used before emptying the cache.
     *
     * @param memoryShare the share of heap size which can be used before
     * emptying the cache
     */
    public void setMemoryShare(double memoryShare) {
        this.memoryShare = memoryShare;
        try {
            loadObjectMutex.acquire();
            updateCache();
            loadObjectMutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the objects if present in the cache. Null if not.
     *
     * @param objectKey the key of the object
     *
     * @return the object of interest, null if not present in the cache
     */
    public Object getObject(Long objectKey) {

        Object object = null;

        DbMutex.loadObjectMutex.acquire();

        if (loadedObjects.containsKey(objectKey)) {

            object = loadedObjects.get(objectKey);

        }

        loadObjectMutex.release();

        return object;
    }

    /**
     * Removes an object from the cache.
     *
     * @param objectKey the key of the object
     *
     * @return the class name of the object
     */
    public String removeObject(long objectKey) {

        String className = null;

        loadObjectMutex.acquire();

        if (!readOnly) {
            if (loadedObjects.containsKey(objectKey)) {
                className = loadedObjects.get(objectKey).getClass().getSimpleName();
                loadedObjects.remove(objectKey);
                objectQueue.removeFirstOccurrence(objectKey);
            }
        }

        loadObjectMutex.release();

        return className;
    }

    /**
     * Adds an object to the cache. The object must not necessarily be in the
     * database. If an object is already present with the same identifiers, it
     * will be silently overwritten.
     *
     * @param objectKey the key of the object
     * @param object the object to store in the cache
     */
    public void addObject(Long objectKey, Object object) {

        loadObjectMutex.acquire();

        if (!readOnly) {

            if (!loadedObjects.containsKey(objectKey)) {

                loadedObjects.put(objectKey, object);
                objectQueue.add(objectKey);

                if (objectsDB.getCurrentAdded() > numToCommit) {
                    objectsDB.commit();
                }
            }

            updateCache();

        }

        loadObjectMutex.release();

    }

    /**
     * Adds an object to the cache. The object must not necessarily be in the
     * database. If an object is already present with the same identifiers, it
     * will be silently overwritten.
     *
     * @param objects the key / objects to store in the cache
     *
     */
    public void addObjects(HashMap<Long, Object> objects) {

        loadObjectMutex.acquire();

        if (!readOnly) {

            loadedObjects.putAll(objects);
            objectQueue.addAll(objects.keySet());

            if (objectsDB.getCurrentAdded() > numToCommit) {
                objectsDB.commit();
            }

            updateCache();
        }

        loadObjectMutex.release();

    }

    /**
     * Indicates whether the memory used by the application is lower than 99% of
     * the heap size.
     *
     * @return a boolean indicating whether the memory used by the application
     * is lower than 75% of the heap
     */
    private boolean memoryCheck() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() < (long) (memoryShare * Runtime.getRuntime().maxMemory());
    }

    /**
     * Saves an entry in the database if modified and clears it from the cache.
     *
     * @param numLastEntries number of keys of the entries
     */
    public void saveObjects(int numLastEntries) {
        loadObjectMutex.acquire();
        saveObjects(numLastEntries, null, true);
        loadObjectMutex.release();
    }

    /**
     * Saves an entry in the database if modified.
     *
     * @param numLastEntries number of keys of the entries
     * @param waitingHandler a waiting handler displaying progress to the user.
     * Can be null. Progress will be displayed as secondary.
     * @param clearEntries a boolean indicating whether the entry shall be
     * cleared from the cache
     */
    public void saveObjects(int numLastEntries, WaitingHandler waitingHandler, boolean clearEntries) {

        if (!readOnly) {

            ListIterator<Long> listIterator = objectQueue.listIterator();
            PersistenceManager pm = objectsDB.getDB();

            for (int i = 0; i < numLastEntries && objectQueue.size() > 0; ++i) {

                if (waitingHandler != null) {

                    waitingHandler.increaseSecondaryProgressCounter();

                    if (waitingHandler.isRunCanceled()) {

                        break;

                    }
                }

                long key = clearEntries ? objectQueue.pollFirst() : listIterator.next();

                Object obj = loadedObjects.get(key);

                if (!((DbObject) obj).jdoZooIsPersistent()) {

                    pm.makePersistent(obj);
                    objectsDB.getIdMap().put(key, ((DbObject) obj).jdoZooGetOid());

                }

                if (clearEntries) {
                    loadedObjects.remove(key);
                }

            }

            objectsDB.commit();

        }

    }

    /**
     * Updates the cache according to the memory settings.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    private void updateCache() {

        while (loadedObjects.size() > keepObjectsThreshold && !memoryCheck()) {

            int toRemove = loadedObjects.size() >> 2;
            saveObjects(toRemove, null, true);

            // turning on the garbage collector from time to time
            // helps to keep the memory clean. Performance becomes
            // better, the mass for cleaning is lower
            System.gc();

        }
    }

    /**
     * Check if key in cache.
     *
     * @param longKey key of the entry
     * @return if key in cache
     */
    public boolean inCache(long longKey) {
        return loadedObjects.containsKey(longKey);
    }

    /**
     * Saves the cache content in the database.
     *
     * @param waitingHandler a waiting handler on which the progress will be
     * displayed
     * @param emptyCache boolean indicating whether the cache content shall be
     * cleared while saving displayed as secondary progress. Can be null.
     */
    public void saveCache(WaitingHandler waitingHandler, boolean emptyCache) {

        loadObjectMutex.acquire();

        // save in batches to enable progress display
        int numSaveIterations = (int) Math.ceil(((double) loadedObjects.size()) / numToCommit); // @TODO: optimize batch size?

        if (waitingHandler != null) {
            waitingHandler.resetSecondaryProgressCounter();
            waitingHandler.setMaxSecondaryProgressCounter(loadedObjects.size() + 1); // @TODO: can this number get bigger than the max integer value?
        }

        for (int i = 0; i < numSaveIterations; i++) {
            if (loadedObjects.size() > numToCommit) {
                saveObjects(numToCommit, waitingHandler, emptyCache);
            } else {
                saveObjects(loadedObjects.size(), waitingHandler, emptyCache);
            }
        }

        if (waitingHandler != null) {

            waitingHandler.setSecondaryProgressCounterIndeterminate(true);

        }

        loadObjectMutex.release();

    }

    /**
     * Indicates whether the cache is empty.
     *
     * @return a boolean indicating whether the cache is empty
     */
    public boolean isEmpty() {
        return loadedObjects.isEmpty();
    }

    /**
     * Clears the cache.
     */
    public void clearCache() {
        loadedObjects.clear();
        objectQueue.clear();
    }

    /**
     * Sets the cache in read only.
     *
     * @param readOnly boolean indicating whether the cache should be in read
     * only
     */
    public void setReadOnly(boolean readOnly) {

        loadObjectMutex.acquire();

        this.readOnly = readOnly;

        loadObjectMutex.release();

    }
}
