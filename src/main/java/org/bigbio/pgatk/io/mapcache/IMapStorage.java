package org.bigbio.pgatk.io.mapcache;

import org.bigbio.pgatk.io.common.PgatkIOException;

import java.io.Serializable;

/**
 * This interface provides general methods to retrieve information from A cache Map implementation such as
 * {@link org.bigbio.pgatk.io.properties.LevelDBPropertyStorage} or {@link net.openhft.chronicle.map.ChronicleMap}
 * @param <V>
 *
 * @author ypriverol
 */
public interface IMapStorage<V> extends Serializable {

    /**
     * Get size of the storage
     * @return number of elements in the Storage.
     */
    long storageSize();

    /**
     * Close a MapStore
     */
    void close() throws PgatkIOException;

    /**
     * Store a particular key and value in the Storage
     * @param key String key
     * @param value value for the key
     */
    void put(String key, V value);

    /**
     * Retrieve a stored property for a defined item. Retruns NULL in case
     * the property has not been set.
     * @param key
     * @throws IndexOutOfBoundsException In case no item with this id exists.
     */
    String get(String key) throws PgatkIOException;

    /**
     * Delete all the information from the Storage
     */
    void cleanStorage() throws PgatkIOException;

}
