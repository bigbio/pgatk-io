package org.bigbio.pgatk.io.properties;

import org.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class InMemoryPropertyStorageTest {

    @Test
    public void setUp() {
        IPropertyStorage storage = new InMemoryPropertyStorage();

        String spec1 = "12345";
        String spec2 = "43232";

        storage.storeProperty(spec1, "RT", "1234");
        storage.storeProperty(spec2, "RT", "1234");

        Assert.assertEquals("1234", storage.getProperty(spec1, "RT"));
        Assert.assertEquals("1234", storage.getProperty(spec2, "RT"));
        Assert.assertNull(storage.getProperty(spec1, "Hallo"));

        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertTrue(storage.getAvailableProperties().contains("RT"));
    }


    @Test
    public void performanceStorageTest(){

        long time = System.currentTimeMillis();
        IPropertyStorage storage = new InMemoryPropertyStorage();

        Random random = new Random();

        for(int i = 0; i < 100000; i++){
            storage.storeProperty(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(100000, storage.storageSize());

        for( int i = 0; i < 40; i++){
            System.out.println(storage.getProperty(String.valueOf(random.nextInt((100000) + 1)),"RT"));
        }

        System.out.println((System.currentTimeMillis() - time) / 1000);

    }

    @Test
    public void writeReadToFile() {

        long time = System.currentTimeMillis();
        IPropertyStorage storage = new InMemoryPropertyStorage();
        IPropertyStorage storageReader = new InMemoryPropertyStorage();

        Random random = new Random();

        for(int i = 0; i < 100000; i++){
            storage.storeProperty(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(100000, storage.storageSize());

        for( int i = 0; i < 40; i++){
            System.out.println(storage.getProperty(String.valueOf(random.nextInt((100000) + 1)),"RT"));
        }

        try {
            File tempFile = File.createTempFile("tempFile", InMemoryPropertyStorage.IN_MEMORY_EXT);
            storage.saveToFile(tempFile.getAbsolutePath());
            storageReader.readFromFile(tempFile.getAbsolutePath());
            for( int i = 0; i < 40; i++){
                int value = random.nextInt((100000) + 1);
                Assert.assertEquals(storageReader.getProperty(String.valueOf(value),"RT"), storage.getProperty(String.valueOf(value),"RT"));
            }
            tempFile.deleteOnExit();
        } catch (IOException | PgatkIOException e) {
            e.printStackTrace();
        }

        System.out.println((System.currentTimeMillis() - time) / 1000);




    }
}
