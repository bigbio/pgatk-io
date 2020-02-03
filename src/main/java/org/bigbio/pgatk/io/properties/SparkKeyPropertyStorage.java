package org.bigbio.pgatk.io.properties;

import com.spotify.sparkey.CompressionType;
import com.spotify.sparkey.Sparkey;
import com.spotify.sparkey.SparkeyReader;
import com.spotify.sparkey.SparkeyWriter;
import org.bigbio.pgatk.io.common.PgatkIOException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class SparkKeyPropertyStorage<T> extends InMemoryPropertyStorage {

    private final File dbDirectory;
    File dbFile;
    HashSet<SparkeyWriter> writerSet = new HashSet<>();
    HashSet<SparkeyReader> readerSet = new HashSet<>();
    long entryCounter = 0;

    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public SparkKeyPropertyStorage(File dbDirectory) throws IOException{
        dbFile = new File(dbDirectory, "properties-" + System.nanoTime() + ".spi");
        this.dbDirectory = dbDirectory;
    }

    private final ThreadLocal<SparkeyWriter> writers = new ThreadLocal<SparkeyWriter>() {
        @Override
        protected SparkeyWriter initialValue() {
            try {
                SparkeyWriter writer = Sparkey.appendOrCreate(dbFile, CompressionType.SNAPPY, 512);
                writerSet.add(writer);
                return writer;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    private final ThreadLocal<SparkeyReader> readers = new ThreadLocal<SparkeyReader>() {
        @Override
        protected SparkeyReader initialValue() {
            try {
                SparkeyReader reader = Sparkey.open(dbFile);
                readerSet.add(reader);
                return reader;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    private byte[] serialize(String value){
        return Base64.getDecoder().decode(Base64.getEncoder().encodeToString( value.getBytes( DEFAULT_CHARSET )));
    }

    private String deserialize(byte[] bytes){
        return new String(bytes, DEFAULT_CHARSET);
    }

    @Override
    public void put(String itemId, String propertyName, String propertyValue) throws PgatkIOException{
        String key = getCombinedKey(itemId, propertyName);
        propertyNames.add(propertyName);
        try {
            writers.get().put( serialize(key), serialize(propertyValue));
        }catch (IOException ex){
            throw new PgatkIOException("Error wiring the following property - " + itemId + " " + propertyName + " " + propertyValue);
        }
        entryCounter++;
    }

    @Override
    public String get(String itemId, String propertyName) throws PgatkIOException {
        String key = getCombinedKey(itemId, propertyName);
        try{
            String value =  deserialize(readers.get().getAsByteArray(serialize(key)));
            return value;
        }catch (IOException ex){
            throw new PgatkIOException("Error retrieving the value for key -- " + itemId + " " + propertyName);
        }
    }

    @Override
    public void put(String key, Object value) {
        put( serialize(key),  serialize((String) value));
    }

    @Override
    public String get(String key) throws PgatkIOException {
        return deserialize(get(serialize(key)));
    }

    @Override
    protected String getCombinedKey(String itemId, String propertyName) {
        return super.getCombinedKey(itemId, propertyName);
    }

    @Override
    public Set<String> getAvailableProperties() {
        return super.getAvailableProperties();
    }

    @Override
    public long storageSize() {
        return entryCounter;
    }

    @Override
    public void cleanStorage() throws PgatkIOException {
        super.cleanStorage();
        entryCounter = 0;
    }

    private void put(byte[] key, byte[] value) {
        try {
            writers.get().put(key, value);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] get(byte[] key) {
        try {
            return readers.get().getAsByteArray(key);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws PgatkIOException{
        for (SparkeyWriter w : writerSet) {
            try {
                w.writeHash();
                w.close();
            } catch (IOException ex) {
                // do nothings.
            }
        }
        for (SparkeyReader r : readerSet) {
            r.close();
        }

        super.cleanFilePersistence(dbDirectory);
        if(dbFile != null && dbFile.exists()){
            dbFile.deleteOnExit();
        }
    }

    public void flush() throws PgatkIOException{
        try {
            writers.get().flush();
            writers.get().writeHash();
        }catch (IOException ex){
            throw new PgatkIOException("Error wiring the SparkKey DB -- " + ex.getMessage());
        }

    }

}
