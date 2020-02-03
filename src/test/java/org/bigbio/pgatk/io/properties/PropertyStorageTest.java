package org.bigbio.pgatk.io.properties;

import lombok.extern.slf4j.Slf4j;
import org.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class PropertyStorageTest {

    int MAX_ENTRY_TEST = 10_000_000;
    int MAX_READING_TEST = 200_000;

    @Test
    public void inMemoryTest() {
        InMemoryPropertyStorage storage = new InMemoryPropertyStorage();

        String spec1 = "12345";
        String spec2 = "43232";
        try {
            storage.put(spec1, "RT", "1234");
            storage.put(spec2, "RT", "1234");
            Assert.assertEquals("1234", storage.get(spec1, "RT"));
            Assert.assertEquals("1234", storage.get(spec2, "RT"));
            Assert.assertNull(storage.get(spec1, "Hallo"));
        } catch (PgatkIOException e) {
            e.printStackTrace();
        }


        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertTrue(storage.getAvailableProperties().contains("RT"));
    }


    @Test
    public void inMemoryLargeTest(){

        long time = System.currentTimeMillis();
        InMemoryPropertyStorage storage = new InMemoryPropertyStorage();

        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            try{
                storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
            }catch (PgatkIOException ex){
                log.error("Error adding value");
            }
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        System.out.println("HashMap: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("HashMap: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

    }

    @Test
    public void readDynamicLevelDBPropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        LevelDBPropertyStorage<String> storage = (LevelDBPropertyStorage<String>) PropertyStorageFactory.buildDynamicLevelDBPropertyStorage(Files.createTempDirectory("properties-").toFile());
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        System.out.println("LevelDB: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("LevelDB: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();


    }

    @Test
    public void readDynamicMapDBPropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        MapDBPropertyStorage<String> storage = (MapDBPropertyStorage<String>) PropertyStorageFactory.buildDynamicMapDBStorage(Files.createTempDirectory("properties-").toFile());
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        System.out.println("MapDB: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("MapDB: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();
    }

    @Test
    public void readDynamicSparkKeyPropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        SparkKeyPropertyStorage<String> storage = (SparkKeyPropertyStorage<String>) PropertyStorageFactory.buildDynamicSparkKeyStorage(Files.createTempDirectory("properties-").toFile());
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        storage.flush();
        System.out.println("SparkKey: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("SparkKey: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();
    }

    @Test
    public void readStaticPropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        ChronicleMapPropertyStorage<String> storage = (ChronicleMapPropertyStorage<String>) PropertyStorageFactory.buildStaticPropertyStorage(Files.createTempDirectory("temp").toFile(), MAX_ENTRY_TEST);
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }

        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        System.out.println("ChronicleMap: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("ChronicleMap: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();
    }
}
