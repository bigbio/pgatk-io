package org.bigbio.pgatk.io.properties;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class InMemoryPropertyStorage implements IPropertyStorage {

    public final static String PROPERTY_BINARY_EXT = ".pser";

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
    public void toBinaryStorage(String filePath) throws PgatkIOException {
        if(!filePath.endsWith(PROPERTY_BINARY_EXT))
            throw new PgatkIOException("The provided extension for the property in memory file" +
                    " is not allow -- " + filePath + " - It should be " + PROPERTY_BINARY_EXT);
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            for(Map.Entry entry: this.propertyStorage.entrySet()){
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                String propertyName = "";
                Optional<String> stringOptional = this.propertyNames.stream()
                        .filter(x -> key.endsWith(x)).findAny();
                if(stringOptional.isPresent())
                    propertyName = stringOptional.get();
                try {
                    objectOutputStream.writeObject(transformSerializablePropertyStorage(key,
                            propertyName, value));
                } catch (IOException e) {
                     log.error("The object with key -- " + key + " " + " can be written into BinaryFile");
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform the structure into a Serialize object.
     * @return List of {@link BinaryPropertyStorage}
     */
    protected BinaryPropertyStorage transformSerializablePropertyStorage(String key, String propertyName,
                                                                      String value) {
        return new BinaryPropertyStorage(key, propertyName, value);
    }

    @Override
    public void fromBinaryStorage(String filePath) throws PgatkIOException {
        if(!filePath.endsWith(PROPERTY_BINARY_EXT))
            throw new PgatkIOException("The provided extension for the property in memory file is " +
                    "not allow -- " + filePath + " - It should be " + PROPERTY_BINARY_EXT);

        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "r");
            FileInputStream fos = new FileInputStream(raf.getFD());
            ObjectInputStream objectInputStream = new ObjectInputStream(fos);
            cleanStorage();
            boolean endFile = false;
            while (!endFile) {
                try {
                    BinaryPropertyStorage acc = (BinaryPropertyStorage) objectInputStream.readObject();
                    this.propertyStorage.put(acc.getKey(), acc.getValue());
                    this.propertyNames.add(acc.getPropertyName());
                }catch (EOFException e){
                    log.info("End of the file found");
                    objectInputStream.close();
                    endFile = true;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanStorage() throws PgatkIOException{
        this.propertyStorage = new HashMap<>();
        this.propertyNames = new HashSet<>();
    }


}
