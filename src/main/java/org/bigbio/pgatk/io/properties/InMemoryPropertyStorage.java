package org.bigbio.pgatk.io.properties;

import org.bigbio.pgatk.io.common.PgatkIOException;

import java.io.*;
import java.util.*;


/**
 * This implementation stores all properties in memory. It is therefore
 * not suggested to use this implementation in production systems but
 * primarily in testing environments or cases where only small amounts
 * of data are being processed.
 *
 * @author jg
 * @author ypriverol
 */
public class InMemoryPropertyStorage implements IPropertyStorage {

    public final static String IN_MEMORY_EXT = ".pin";

    protected Map<String, String> propertyStorage;
    protected Set<String> propertyNames = new HashSet<>(20);

    public InMemoryPropertyStorage() {
        propertyStorage = new HashMap<>(20_000);
    }

    @Override
    public void storeProperty(String itemId, String propertyName, String propertyValue) {
        propertyNames.add(propertyName);
        propertyStorage.put(getStorageKey(itemId, propertyName), propertyValue);
    }

    @Override
    public String getProperty(String itemId, String propertyName) throws IndexOutOfBoundsException {
        return propertyStorage.get(getStorageKey(itemId, propertyName));
    }

    protected String getStorageKey(String itemId, String propertyName) {
        return itemId + propertyName;
    }

    @Override
    public Set<String> getAvailableProperties() {
        return Collections.unmodifiableSet(propertyNames);
    }

    public int storageSize(){
        return propertyStorage.size();
    }

    @Override
    public void close() throws IOException {
        propertyStorage = null;
    }

    @Override
    public void saveToFile(String filePath) throws PgatkIOException {
        if(!filePath.endsWith(IN_MEMORY_EXT))
            throw new PgatkIOException("The provided extension for the property in memory file is not allow -- " + filePath + " - It should be " + IN_MEMORY_EXT);
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            ObjectOutputStream objectOut = new ObjectOutputStream(fos);
            List<PropertyStorage> properties = transformSerializablePropertyStorage();
            objectOut.writeObject(properties);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform the structure into a Serialize object.
     * @return List of {@link PropertyStorage}
     */
    private List<PropertyStorage> transformSerializablePropertyStorage() {
        List<PropertyStorage> propertySerializeList = new ArrayList<>();
        this.propertyStorage.forEach((key, value) -> {
            Optional<String> propertyOptional = propertyNames.stream().filter(key::endsWith).findAny();
            String propertyName = "";
            if (propertyOptional.isPresent())
                propertyName = propertyOptional.get();
            propertySerializeList.add(new PropertyStorage(key, propertyName, value));
        });
        return propertySerializeList;
    }

    @Override
    public void readFromFile(String filePath) throws PgatkIOException {
        if(!filePath.endsWith(IN_MEMORY_EXT))
            throw new PgatkIOException("The provided extension for the property in memory file is not allow -- " + filePath + " - It should be " + IN_MEMORY_EXT);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filePath));
            List<PropertyStorage> value = (List<PropertyStorage>) in.readObject();
            this.propertyStorage = new HashMap<>();
            this.propertyNames = new HashSet<>();
            value.forEach( x-> {
                this.propertyStorage.put(x.getKey(), x.getValue());
                this.propertyNames.add(x.getPropertyName());
            });
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public class PropertyStorage implements Serializable {

        String key;
        String value;
        String propertyName;

        public PropertyStorage(String key, String propertyName, String value) {
            this.key = key;
            this.value = value;
            this.propertyName = propertyName;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }
    }

}
