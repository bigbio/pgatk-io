package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * This implementation store a set of keys into a java {@link HashMap}. It is therefore not suggested to use this implementation in production systems but
 * primarily in testing environments or cases where only small amounts of data are being processed.

 * @author jg
 * @author ypriverol
 */
@Slf4j
public class InMemoryPropertyStorage implements IPropertyStorage{

    protected Map<String, String> propertyStorage;
    protected Set<String> propertyNames = new HashSet<>(20);

    public InMemoryPropertyStorage() {
        propertyStorage = new HashMap<>(20_000);
    }

    /**
     * This function store a particular property from the store system, in this case
     * the key is the combination of the itemId and the propertyName
     * @param itemId Item identifier
     * @param propertyName property Name
     * @param propertyValue property Value
     */
    @Override
    public void put(String itemId, String propertyName, String propertyValue) throws PgatkIOException {
        propertyNames.add(propertyName);
        put(getCombinedKey(itemId, propertyName), propertyValue);
    }



    /**
     * Get the Value for an specific combination of itemId and propertyName
     * @param itemId Item identifier
     * @param propertyName Property Name
     * @return Value of the combination of item id + property name
     * @throws IndexOutOfBoundsException
     */
    public String get(String itemId, String propertyName) throws PgatkIOException {
        return get(getCombinedKey(itemId, propertyName));
    }

    /**
     * Put a key, value in the storage
     * @param key String key
     * @param value value for the key
     */
    @Override
    public void put(String key, Object value) {
        propertyStorage.put(key, (String) value);
    }

    /**
     * Get a value for a key in the Map
     * @param key key to be retrirve
     * @return Value
     * @throws IndexOutOfBoundsException
     */
    @Override
    public String get(String key) throws PgatkIOException {
        return propertyStorage.get(key);
    }

    /**
     * Get a combined key from an ItemId and Property Name
     * @param itemId Item identifier
     * @param propertyName Property identifier
     * @return Combined Key
     */
    protected String getCombinedKey(String itemId, String propertyName) {
        return itemId + propertyName;
    }

    /**
     * Properties available in the Map
     * @return Set of Properties
     */
    public Set<String> getAvailableProperties() {
        return Collections.unmodifiableSet(propertyNames);
    }

    /**
     * Get the Size of the Storage
     * @return Number of elements
     */
    @Override
    public long storageSize(){
        return propertyStorage.size();
    }

    /**
     * Close the storage Map
     * @throws IOException
     */
    @Override
    public void close() throws PgatkIOException {
        propertyStorage = null;
    }

    @Override
    public void flush() throws PgatkIOException {
        // this method has no effect on the in-memory storage
    }

    //    @Override
//    public void toBinaryStorage(String filePath) throws PgatkIOException {
//        if(!filePath.endsWith(PROPERTY_BINARY_EXT))
//            throw new PgatkIOException("The provided extension for the property in memory file" +
//                    " is not allow -- " + filePath + " - It should be " + PROPERTY_BINARY_EXT);
//        try {
//            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
//            FileOutputStream fos = new FileOutputStream(raf.getFD());
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
//            for(Map.Entry entry: this.propertyStorage.entrySet()){
//                String key = (String) entry.getKey();
//                String value = (String) entry.getValue();
//                String propertyName = "";
//                Optional<String> stringOptional = this.propertyNames.stream()
//                        .filter(x -> key.endsWith(x)).findAny();
//                if(stringOptional.isPresent())
//                    propertyName = stringOptional.get();
//                try {
//                    objectOutputStream.writeObject(transformSerializablePropertyStorage(key,
//                            propertyName, value));
//                } catch (IOException e) {
//                     log.error("The object with key -- " + key + " " + " can be written into BinaryFile");
//                }
//            }
//            raf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Transform the structure into a Serialize object.
//     * @return List of {@link BinaryPropertyStorage}
//     */
//    protected BinaryPropertyStorage transformSerializablePropertyStorage(String key, String propertyName,
//                                                                      String value) {
//        return new BinaryPropertyStorage(key, propertyName, value);
//    }

//    @Override
//    public void fromBinaryStorage(String filePath) throws PgatkIOException {
//        if(!filePath.endsWith(PROPERTY_BINARY_EXT))
//            throw new PgatkIOException("The provided extension for the property in memory file is " +
//                    "not allow -- " + filePath + " - It should be " + PROPERTY_BINARY_EXT);
//
//        try {
//            RandomAccessFile raf = new RandomAccessFile(filePath, "r");
//            FileInputStream fos = new FileInputStream(raf.getFD());
//            ObjectInputStream objectInputStream = new ObjectInputStream(fos);
//            cleanStorage();
//            boolean endFile = false;
//            while (!endFile) {
//                try {
//                    BinaryPropertyStorage acc = (BinaryPropertyStorage) objectInputStream.readObject();
//                    this.propertyStorage.put(acc.getKey(), acc.getValue());
//                    this.propertyNames.add(acc.getPropertyName());
//                }catch (EOFException e){
//                    log.info("End of the file found");
//                    objectInputStream.close();
//                    endFile = true;
//                }
//            }
//
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void cleanStorage() throws PgatkIOException{
        this.propertyStorage = new HashMap<>();
        this.propertyNames = new HashSet<>();
    }

    /**
     * Clean if some persistence of the Maps in files is present
     * @param filePath Path that contains the Persistence files
     * @throws PgatkIOException Error if File access
     */
    public void cleanFilePersistence(File filePath) throws PgatkIOException {
        try {
            Files.walk(filePath.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new PgatkIOException("Error deleting the persistence files  -- " + filePath);
        }
    }
}
