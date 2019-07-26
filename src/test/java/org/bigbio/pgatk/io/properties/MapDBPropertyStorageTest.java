package org.bigbio.pgatk.io.properties;

import org.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * ==Overview==
 *
 * @author ypriverol on 14/10/2018.
 */
public class MapDBPropertyStorageTest {

    @Test
    public void storeProperty() throws IOException, PgatkIOException {

        IPropertyStorage storage = PropertyStorageFactory.buildStaticPropertyStorage(Files.createTempDirectory("temp").toFile(), 200);

        long time = System.currentTimeMillis();
        Random random = new Random();

        for(int i = 0; i < 200; i++){
            storage.storeProperty(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(200, storage.storageSize());

        for( int i = 0; i < 40; i++){
            System.out.println(storage.getProperty(String.valueOf(random.nextInt((200) + 1)),"RT"));
        }

        System.out.println((System.currentTimeMillis() - time) / 1000);

        storage.close();
    }


    @Test
    public void storePropertyDynamic() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        IPropertyStorage storage = PropertyStorageFactory.buildDynamicPropertyStorage(Files.createTempDirectory("temp").toFile());
        Random random = new Random();

        for(int i = 0; i < 200; i++){
            storage.storeProperty(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(200, storage.storageSize());

        for( int i = 0; i < 40; i++){
            System.out.println(storage.getProperty(String.valueOf(random.nextInt((200) + 1)),"RT"));
        }

        System.out.println((System.currentTimeMillis() - time) / 1000);

        storage.close();
    }

    @Test
    public void writeAndreadToFile() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        IPropertyStorage storage = PropertyStorageFactory.buildDynamicPropertyStorage(Files.createTempDirectory("temp").toFile());
        IPropertyStorage storageReader = PropertyStorageFactory.buildDynamicPropertyStorage(Files.createTempDirectory("temp").toFile());
        Random random = new Random();

        for(int i = 0; i < 200; i++){
            storage.storeProperty(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }

        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(200, storage.storageSize());

        for( int i = 0; i < 40; i++){
            System.out.println(storage.getProperty(String.valueOf(random.nextInt((200) + 1)),"RT"));
        }

        File tempFile = File.createTempFile("tempFile", InMemoryPropertyStorage.IN_MEMORY_EXT);
        storage.saveToFile(tempFile.getAbsolutePath());
        storageReader.readFromFile(tempFile.getAbsolutePath());
        for( int i = 0; i < 40; i++){
            int value = random.nextInt((100000) + 1);
            Assert.assertEquals(storageReader.getProperty(String.valueOf(value),"RT"), storage.getProperty(String.valueOf(value),"RT"));
        }

        System.out.println((System.currentTimeMillis() - time) / 1000);

        storage.close();


    }
}