package org.bigbio.pgatk.io.properties;

import lombok.extern.slf4j.Slf4j;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 *  
 *  This class can be used to store properties well millions of Spectra are under analysis.
 *  The current implementation allows to store 1 millions of properties with less than 200 MG of
 *  memory.
 *
 *  Be ware that this implementation is slower than {@link InMemoryPropertyStorage} but consume
 *  10 times less memory than the in memory tool. But it is 2 time slower than the {@link InMemoryPropertyStorage}.
 *
 *  Please Review this benchmark for different implementations of Key-Value Stores
 *  (https://github.com/lmdbjava/benchmarks/blob/master/results/20160710/README.md)
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 *
 * @author ypriverol on 14/10/2018.
 */
@Slf4j
@Deprecated
public class IMapDBPropertyStorage extends InMemoryPropertyStorage {

//    public static File dbFile = null;
//
//    public static final long MAX_NUMBER_FEATURES = 100000000;
//    private long numberProperties;
//    private DB levelDB = null;
//    private int levelDBSize = 0;
//
//    public boolean dynamic = false;
//
//    public IMapDBPropertyStorage(File dbDirectory, boolean dynamic, long numberProperties) throws IOException {
//
//        // Create the file that will store the persistence database
//        this.dynamic = dynamic;
//
//        if(numberProperties == -1)
//            this.numberProperties = MAX_NUMBER_FEATURES;
//        else
//            this.numberProperties = numberProperties;
//
//        if(dynamic){
//            log.info("----- LEVELDB MAP ------------------------");
//            dbFile = new File(dbDirectory.getAbsolutePath() + File.separator + "properties-" + System.nanoTime() + PROPERTY_BINARY_EXT);
//            if(!dbFile.exists())
//                dbFile.mkdirs();
//            Options options = new Options();
//            options.cacheSize(100 * 1048576); // 100MB cache
//            options.createIfMissing(true);
//            options.compressionType(CompressionType.SNAPPY);
//            levelDB = Iq80DBFactory.factory.open(dbFile, options);
//
//        }else{
//            log.info("----- CHRONICLE MAP ------------------------");
//            dbFile = new File(dbDirectory.getAbsolutePath() + File.separator + "properties-" + System.nanoTime() + PROPERTY_BINARY_EXT);
//            dbFile.deleteOnExit();
//            this.propertyStorage =
//                    ChronicleMapBuilder.of(String.class, String.class)
//                            .entries(numberProperties) //the maximum number of entries for the map
//                            .averageKeySize(64)
//                            .averageValueSize(54)
//                            .createPersistedTo(dbFile);
//        }
//
//    }
//
//    public IMapDBPropertyStorage(File dbDirectory, int numberProperties) throws IOException {
//
//        log.info("----- CHRONICLE MAP ------------------------");
//        dbFile = new File(dbDirectory.getAbsolutePath() + File.separator + "properties-" + System.nanoTime() + ".db");
//        dbFile.deleteOnExit();
//        this.propertyStorage =
//                ChronicleMapBuilder.of(String.class, String.class)
//                        .entries(numberProperties) //the maximum number of entries for the map
//                        .averageKeySize(64)
//                        .averageValueSize(54)
//                        .createPersistedTo(dbFile);
//    }
//
//    @Override
//    public void storeProperty(String itemId, String propertyName, String propertyValue) {
//        propertyNames.add(propertyName);
//        if(dynamic){
//            levelDB.put(Iq80DBFactory.bytes(getCombinedKey(itemId, propertyName)), Iq80DBFactory.bytes(propertyValue));
//            levelDBSize++;
//        }
//        else
//            propertyStorage.put(getCombinedKey(itemId, propertyName), propertyValue);
//    }
//
//    @Override
//    public String getProperty(String itemId, String propertyName) throws IndexOutOfBoundsException {
//        String value;
//        if(dynamic)
//            value = Iq80DBFactory.asString(levelDB.get(Iq80DBFactory.bytes(getCombinedKey(itemId, propertyName))));
//        else
//            value = super.getProperty(itemId, propertyName);
//        return value;
//    }
//
//    @Override
//    public int storageSize() {
//        if(dynamic)
//            return levelDBSize;
//        return super.storageSize();
//    }
//
//    /**
//     * Close the DB on Disk and delete it.
//     */
//    @Override
//    public void close() throws IOException {
//        if(dynamic)
//            levelDB.close();
//
//        if(dbFile != null && dbFile.exists()){
//            dbFile.deleteOnExit();
//        }
//
//    }
//
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
//
//    /**
//     * Transform the structure into a Serialize object.
//     * @return List of {@link BinaryPropertyStorage}
//     */
//
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
//
//    @Override
//    public void cleanStorage() throws PgatkIOException{
//        try {
//            if(dynamic){
//                log.info("----- LEVELDB MAP ------------------------");
//                levelDB.close();
//                FileUtils.deleteDirectory(new File(dbFile.getAbsolutePath()));
//                if(!dbFile.exists())
//                    dbFile.mkdirs();
//                Options options = new Options();
//                options.cacheSize(100 * 1048576)
//                        .createIfMissing(true)
//                        .writeBufferSize(4096)
//                        .compressionType(CompressionType.SNAPPY);
//                levelDB = Iq80DBFactory.factory.open(dbFile, options);
//                levelDBSize = 0;
//            }else{
//                log.info("----- CHRONICLE MAP ------------------------");
//                dbFile.deleteOnExit();
//                this.propertyStorage =
//                        ChronicleMapBuilder.of(String.class, String.class)
//                                //the maximum number of entries for the map
//                                .entries(numberProperties)
//                                .averageKeySize(64)
//                                .averageValueSize(54)
//                                .createPersistedTo(dbFile);
//
//            }
//        } catch (IOException e) {
//            throw new PgatkIOException("Error cleaning the database");
//        }
//        this.propertyNames = new HashSet<>();
//    }
}
