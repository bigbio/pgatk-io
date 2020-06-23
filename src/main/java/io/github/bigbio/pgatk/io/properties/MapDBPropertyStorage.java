package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MapDBPropertyStorage<T> extends InMemoryPropertyStorage{

    private final File directoryPath;
    DB db;
    File dbFile;

    public MapDBPropertyStorage(File directoryPath) throws IOException{

        log.info(" ------------- MapDB Created -------------");
        this.directoryPath = directoryPath;
        dbFile = new File(directoryPath.getAbsolutePath() + File.separator + "properties-" + System.nanoTime() + ".db");
        dbFile.deleteOnExit();
        db = DBMaker
                .fileDB(dbFile)
                .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
                .fileMmapPreclearDisable()   // Make mmap file faster
                // Unmap (release resources) file when its closed.
                // That can cause JVM crash if file is accessed after it was unmapped
                // (there is possible race condition).
                .cleanerHackEnable()
                .allocateStartSize(1 * 1024*1024*1024)  // 10GB
                .allocateIncrement(512 * 1024*1024)       // 512MB
                .make();
        propertyStorage = db
                .hashMap("map", Serializer.STRING, Serializer.STRING)
                .createOrOpen();

    }

    @Override
    public void put(String itemId, String propertyName, String propertyValue) {
        propertyNames.add(propertyName);
        propertyStorage.put(getCombinedKey(itemId, propertyName), propertyValue);
    }

    @Override
    public String get(String itemId, String propertyName) throws PgatkIOException {
        propertyNames.add(propertyName);
        return propertyStorage.get(getCombinedKey(itemId, propertyName));
    }

    @Override
    public void put(String key, Object value) {
        propertyStorage.put(key, (String) value);
    }

    @Override
    public String get(String key) throws PgatkIOException {
        return propertyStorage.get(key);
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
        return super.storageSize();
    }

    @Override
    public void close() throws PgatkIOException {
        super.cleanFilePersistence(directoryPath);
        if(dbFile != null && dbFile.exists()){
            dbFile.deleteOnExit();
        }
    }

    @Override
    public void cleanStorage() throws PgatkIOException {
        log.info("----- MapDB  ------------------------");
        dbFile.deleteOnExit();
        db = DBMaker
                .fileDB(dbFile)
                .fileMmapEnable()
                .make();
        propertyStorage = db
                .hashMap("map", Serializer.STRING, Serializer.STRING)
                .createOrOpen();
        this.propertyNames = new HashSet<>();
    }
}
