package org.bigbio.pgatk.io.properties;

import org.bigbio.pgatk.io.common.PgatkIOException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

/**
 * This interface describes a property storage used to store
 * the properties of the loaded spectra.
 *
 * @author jg
 * @author ypriverol
 */
public interface IPropertyStorage extends Serializable {
    /**
     * Store a property.
     * @param itemId The item's unique id to store the property for.
     * @param propertyName The property's name
     * @param propertyValue The property's value
     */
    void storeProperty(String itemId, String propertyName, String propertyValue);

    /**
     * Retrieve a stored property for a defined item. Retruns NULL in case
     * the property has not been set.
     * @param itemId The item's unique id to fetch the property for
     * @param propertyName The property's name.
     * @return The property's value as a String.
     * @throws IndexOutOfBoundsException In case no item with this id exists.
     */
    String getProperty(String itemId, String propertyName) throws IndexOutOfBoundsException;

    /**
     * Returns the names of all properties currently available in the storage.
     * @return The available properties.
     */
    Set<String> getAvailableProperties();

    /**
     * Get size of the storage
     * @return number of elements in the Storage.
     */
    int storageSize();

    /**
     * Close a Property Storage.
     */
    void close() throws IOException;

    /**
     * Save the PropertyFile to a different file name
     * @param filePath File Name
     */
    void toBinaryStorage(String filePath) throws PgatkIOException;

    /**
     * This function allows to read properties from a File
     * @param filePath file path
     */
    void fromBinaryStorage(String filePath) throws PgatkIOException;

    /**
     * Delete all the information from the Storage
     */
    void cleanStorage() throws PgatkIOException;
}
