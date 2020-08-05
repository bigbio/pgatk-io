package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class EcachePropertyStorage<V> extends InMemoryPropertyStorage{

    private static final long DISK_SPACE_GB = 4;
    private final PersistentCacheManager cacheManager;
    private final Cache<String, String> cacheStorage;
    private File dbDirectory;
    long entryCounter = 0;
    private static String PROPERTY_ALIAS = "properties-echache"; 
    private static int DEFAULT_NUM_ENTRIES = 100_000;

    public EcachePropertyStorage(File dbDirectory) {
        this.dbDirectory = dbDirectory;

        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(this.dbDirectory))
                .withCache(PROPERTY_ALIAS,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(DEFAULT_NUM_ENTRIES, EntryUnit.ENTRIES)
                                        .disk(DISK_SPACE_GB, MemoryUnit.GB, false)
                        )
                )
                .build(true);

        cacheStorage = cacheManager.getCache(PROPERTY_ALIAS, String.class, String.class);
    }

    @Override
    public void put(String itemId, String propertyName, String propertyValue) {
        String key = getCombinedKey(itemId, propertyName);
        propertyNames.add(propertyName);
        cacheStorage.put(key, propertyValue);
        entryCounter++;
    }

    @Override
    public String get(String itemId, String propertyName) {
        String key = getCombinedKey(itemId, propertyName);
        return cacheStorage.get(key);
    }

    @Override
    public void put(String key, Object value) {
        cacheStorage.put(key, (String) value);
    }

    @Override
    public String get(String key) {
        return cacheStorage.get(key);
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
    public void cleanStorage() {
        cacheStorage.clear();
        entryCounter = 0;
    }


    @Override
    public void close() throws PgatkIOException{
        try {
            cacheManager.close();
            cleanFilePersistence(dbDirectory);
            dbDirectory.deleteOnExit();
        } catch (Exception e) {
            throw new PgatkIOException("Error destroying the Ecache -- " + e.getMessage());
        }

    }

}
