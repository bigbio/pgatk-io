package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

@Slf4j
public class ChronicleMapPropertyStorage<T> extends InMemoryPropertyStorage{

    public static File dbFile = null;


    // This is the number of features + items
    public static final long MAX_NUMBER_FEATURES = 100_000_000;
    private final File dbDirectory;
    private long numberProperties;

    /**
     * Create a {@link net.openhft.chronicle.map.ChronicleMap} for storing properties
     * @param dbDirectory Path to the directory that contains the properties
     * @param numberProperties estimated number of properties
     * @throws IOException
     */
    public ChronicleMapPropertyStorage(File dbDirectory, long numberProperties) throws IOException {

        if(numberProperties == -1)
            this.numberProperties = MAX_NUMBER_FEATURES;
        else
            this.numberProperties = numberProperties;

        log.info("----- CHRONICLE MAP ------------------------");
        this.dbDirectory = dbDirectory;
        dbFile = new File(dbDirectory.getAbsolutePath() + File.separator + "properties-" + System.nanoTime() + ".db");
        dbFile.deleteOnExit();
        this.propertyStorage = ChronicleMapBuilder.of(String.class, String.class)
                .entries(numberProperties) //the maximum number of entries for the map
                .averageKeySize(64)
                .averageValueSize(54)
                .createPersistedTo(dbFile);

    }

    /**
     * Create a {@link net.openhft.chronicle.map.ChronicleMap} for storing properties using the
     * MAX_NUMBER_FEATURES = 100M.
     * @param directoryPath Directory Path
     * @throws IOException
     */
    public ChronicleMapPropertyStorage(File directoryPath) throws IOException {
        this(directoryPath, MAX_NUMBER_FEATURES);
    }

    @Override
    public void put(String itemId, String propertyName, String propertyValue) throws PgatkIOException {
        super.put(itemId, propertyName, propertyValue);
    }

    @Override
    public String get(String itemId, String propertyName) throws PgatkIOException {
        return super.get(itemId, propertyName);
    }

    @Override
    public long storageSize() {
        return super.storageSize();
    }

    /**
     * Close the DB on Disk and delete it.
     */
    @Override
    public void close() throws PgatkIOException {
        super.cleanFilePersistence(dbDirectory);
        if(dbFile != null && dbFile.exists()){
            dbFile.deleteOnExit();
        }
    }

    @Override
    public void cleanStorage() throws PgatkIOException{
        try {
            log.info("----- CHRONICLE MAP ------------------------");
            dbFile.deleteOnExit();
            this.propertyStorage = ChronicleMapBuilder.of(String.class, String.class)
                            //the maximum number of entries for the map
                    .entries(numberProperties)
                    .averageKeySize(64)
                    .averageValueSize(54)
                    .createPersistedTo(dbFile);
        } catch (IOException e) {
            throw new PgatkIOException("Error cleaning the ChronicleMap Store -- " + e.getMessage());
        }
        this.propertyNames = new HashSet<>();
    }
}
