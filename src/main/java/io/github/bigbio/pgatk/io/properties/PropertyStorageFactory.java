package io.github.bigbio.pgatk.io.properties;


import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.mapcache.IMapStorage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * ==Overview==
 *
 * @author ypriverol on 18/10/2018.
 */
public class PropertyStorageFactory {

    /**
     * Get {@link InMemoryPropertyStorage} for the Storage
     * @return IPropertyStorage
     */
    public static Optional<IMapStorage> buildInMemoryPropertyStorage(){
        return Optional.of(new InMemoryPropertyStorage());
    }

    /**
     * Get a Static Property Storage with default version MAX_NUMBER_FEATURES
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildStaticPropertyStorage(File tempDirectory) throws PgatkIOException {
        try {
            return new ChronicleMapPropertyStorage(tempDirectory);
        } catch (IOException e) {
            throw new PgatkIOException("Error building the Dynamic Property Storage --", e.getCause());
        }
    }

    /**
     * Get a Static Property Storage with s predefined number of entries.
     * @param numberProperties Number of properties
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildStaticPropertyStorage(File tempDirectory, int numberProperties) throws PgatkIOException {
        try {
            return new ChronicleMapPropertyStorage(tempDirectory, numberProperties);
        } catch (IOException e) {
            throw new PgatkIOException("Error building the Dynamic Property Storage --", e.getCause());
        }

    }

    /**
     * Get a Static Property Storage with s predefined number of entries.
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildDynamicLevelDBPropertyStorage(File tempDirectory) throws PgatkIOException {
        try {
            return new LevelDBPropertyStorage(tempDirectory);
        } catch (IOException e) {
            throw new PgatkIOException("Error building the Dynamic Property Storage --", e.getCause());
        }
    }


    /**
     * Get a Static Property Storage with s predefined number of entries.
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildDynamicMapDBStorage(File tempDirectory) throws PgatkIOException {
        return new MapDBPropertyStorage(tempDirectory);
    }

    /**
     * Get a Static Property Storage with s predefined number of entries.
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildDynamicSparkKeyStorage(File tempDirectory) throws PgatkIOException {
        return new SparkeyPropertyStorage<>(tempDirectory);
    }

    /**
     * Get a Static Property Storage with s predefined number of entries.
     * @return IPropertyStorage
     */
    public static IPropertyStorage buildDynamicEcacheStorage(File tempDirectory) throws PgatkIOException {
        return new EcachePropertyStorage(tempDirectory);
    }
}
