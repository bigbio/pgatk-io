package org.bigbio.pgatk.io.properties;

import org.bigbio.pgatk.io.common.PgatkIOException;
import org.bigbio.pgatk.io.mapcache.IMapStorage;

/**
 * This interface describes a property storage in HashMap. A property storage contains a value for each property of the spectrum in a key value pair.
 * The key will be the combination of the spectrum identifier + the property name (e.g. RT, or Mz), when the value is the specific value for a property
 * for the specific spectrum.
 *
 * @author jg
 * @author ypriverol
 */
public interface IPropertyStorage extends IMapStorage {

    /**
     * The key in the Property Storage is the combination of itemId + PropertyName
     * For example:
     * @param itemId Item Identifier
     * @param propertyName property Name
     * @param propertyValue Value
     */
    void put(String itemId, String propertyName, String propertyValue) throws PgatkIOException;

    /**
     * The key in the Property Storage is the combination of itemId + PropertyName
     * For example:
     * @param itemId Item Identifier
     * @param propertyName property Name
     * @return propertyValue Value
     */
    String get(String itemId, String propertyName) throws PgatkIOException;

}
