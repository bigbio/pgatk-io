package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * Implementation of LevelDB for {@link InMemoryPropertyStorage} using levelDB database. This implementation allows to
 * to cache in files some
 */
@Slf4j
public class LevelDBPropertyStorage<T> extends InMemoryPropertyStorage{


    public static long LEVELDB_CACHE_SIZE = 100 * 10485760;  //1G
    private static final int LEVELDB_BUFFER_SIZE = 4096;

    private DB levelDB = null;
    private int levelDBSize = 0;
    public static File dbFile = null;
    public File dbDirectory;

    /**
     * The LevelDB Property Storage allows to store key-values in Map and Cache
     * @param dbDirectory Directory for the LevelDB cached files
     * @throws IOException
     */
    public LevelDBPropertyStorage(File dbDirectory) throws IOException {

        log.info("----- LEVELDB MAP ------------------------");

        this.dbDirectory = dbDirectory;
        dbFile = new File(dbDirectory.getAbsolutePath() + File.separator + "properties-" + System.nanoTime());
        if(!dbFile.exists())
              dbFile.mkdirs();

        Options options = new Options();
        options.cacheSize(LEVELDB_CACHE_SIZE); // 100MB cache
        options.createIfMissing(true);
        options.compressionType(CompressionType.SNAPPY);
        levelDB = Iq80DBFactory.factory.open(dbFile, options);

    }

    @Override
    public void put(String itemId, String propertyName, String propertyValue) {
        propertyNames.add(propertyName);
        levelDB.put(Iq80DBFactory.bytes(getCombinedKey(itemId, propertyName)), Iq80DBFactory.bytes(propertyValue));
        levelDBSize++;
    }

    @Override
    public String get(String itemId, String propertyName) {
        return Iq80DBFactory.asString(levelDB.get(Iq80DBFactory.bytes(getCombinedKey(itemId, propertyName))));

    }

    @Override
    public long storageSize() {
        return levelDBSize;
    }

    /**
     * Close the DB on Disk and delete it.
     */
    @Override
    public void close() throws PgatkIOException {
        try {
            levelDB.close();
            super.cleanFilePersistence(dbDirectory);
            if(dbFile != null && dbFile.exists()){
                dbFile.deleteOnExit();
            }
        } catch (IOException e) {
            throw new PgatkIOException("Error closing the persistence storage -- " + e.getMessage());
        }
    }

    @Override
    public void cleanStorage() throws PgatkIOException {
        log.info("----- LEVELDB MAP ------------------------");
        try{
            levelDB.close();
            FileUtils.deleteDirectory(new File(dbFile.getAbsolutePath()));
            if(!dbFile.exists())
                dbFile.mkdirs();
            Options options = new Options();
            options.cacheSize(LEVELDB_CACHE_SIZE)
                    .createIfMissing(true)
                    .writeBufferSize(LEVELDB_BUFFER_SIZE)
                    .compressionType(CompressionType.SNAPPY);
            levelDB = Iq80DBFactory.factory.open(dbFile, options);
            levelDBSize = 0;
            this.propertyNames = new HashSet<>();
        }catch (IOException ex){
            throw new PgatkIOException("Error cleaning the levelDB storage -- " + ex.getMessage());
        }
    }

//    @Override
//    public void toBinaryStorage(String filePath) throws PgatkIOException {
//        if(!dynamic)
//            super.toBinaryStorage(filePath);
//        else{
//            try{
//                WriteBatch batch = levelDB.createWriteBatch();
//                levelDB.write(batch);
//                batch.close();
//                dbLeveltoBinaryStorage(filePath);
//            }catch (IOException e){
//                throw new PgatkIOException("Error compressing the levelDB database");
//            }
//        }
//    }

    /**
     * Transform the structure into a Serialize object.
     * @return List of {@link BinaryPropertyStorage}
     */

//    private void dbLeveltoBinaryStorage(String filePath) throws PgatkIOException{
//
//        if(!filePath.endsWith(PROPERTY_BINARY_EXT))
//            throw new PgatkIOException("The provided extension for the property in memory file" +
//                    " is not allow -- " + filePath + " - It should be " + PROPERTY_BINARY_EXT);
//
//        try {
//            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
//            FileOutputStream fos = new FileOutputStream(raf.getFD());
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
//            DBIterator it = levelDB.iterator();
//            while(it.hasNext()){
//                Map.Entry<byte[], byte[]> entry = it.next();
//                String key = Iq80DBFactory.asString(entry.getKey());
//                String value = Iq80DBFactory.asString(entry.getValue());
//                String propertyName = "";
//                Optional<String> propertyOptional = propertyNames.stream().filter(key::endsWith).findAny();
//                if(propertyOptional.isPresent())
//                    propertyName = propertyOptional.get();
//                try {
//                    objectOutputStream.writeObject(transformSerializablePropertyStorage(key, propertyName, value));
//                } catch (IOException e) {
//                    log.error("The object with key -- " + key + " " + " can be written into BinaryFile");
//                }
//            }
//            objectOutputStream.close();
//            fos.close();
//            raf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void fromBinaryStorage(String filePath) throws PgatkIOException {
//        if(!dynamic)
//            super.fromBinaryStorage(filePath);
//        else
//            readBinaryStorage(filePath);
//    }
//
//    private void readBinaryStorage(String filePath) throws PgatkIOException{
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
//                    levelDB.put(Iq80DBFactory.bytes(acc.getKey()), Iq80DBFactory.bytes(acc.getValue()));
//                    propertyNames.add(acc.getPropertyName());
//                    levelDBSize++;
//                }catch (EOFException e){
//                    log.info("End of the file found");
//                    objectInputStream.close();
//                    endFile = true;
//                }
//            }
//
//        } catch (IOException | ClassNotFoundException | PgatkIOException e) {
//            e.printStackTrace();
//        }
//
//    }


}
