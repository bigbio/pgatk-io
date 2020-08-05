package io.github.bigbio.pgatk.io.properties;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.SpectrumProperty;
import lombok.extern.slf4j.Slf4j;
import io.github.bigbio.pgatk.io.objectdb.LongObject;
import io.github.bigbio.pgatk.io.objectdb.ObjectsDB;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class PropertyStorageTest {

    int MAX_ENTRY_TEST = 100_000;
    int MAX_READING_TEST = 50_000;

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
            int key = random.nextInt(MAX_ENTRY_TEST);
            String value = storage.get(String.valueOf(key), "RT");
        });

        System.out.println("LevelDB: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();

    }

    @Test
    @Ignore
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
            int key = random.nextInt(MAX_ENTRY_TEST);
            String value = storage.get(String.valueOf(key), "RT");
        });

        System.out.println("MapDB: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();
    }

    @Test
    @Ignore
    public void readDynamicEcachePropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        EcachePropertyStorage<String> storage = (EcachePropertyStorage<String>) PropertyStorageFactory
                .buildDynamicEcacheStorage(Files.createTempDirectory("properties-").toFile());
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());


        System.out.println("Ecache: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            int key = random.nextInt(MAX_ENTRY_TEST);
            String value = storage.get(String.valueOf(key), "RT");
        });

        System.out.println("Ecache: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

        storage.close();
    }

    @Test
    public void readDynamicSparkKeyPropertyStorage() throws IOException, PgatkIOException {

        long time = System.currentTimeMillis();
        SparkeyPropertyStorage<String> storage = (SparkeyPropertyStorage<String>) PropertyStorageFactory
                .buildDynamicSparkKeyStorage(Files.createTempDirectory("properties-").toFile());
        Random random = new Random();

        for(int i = 0; i < MAX_ENTRY_TEST; i++){
            storage.put(String.valueOf(i), "RT", String.valueOf(Math.random()));
        }
        Assert.assertEquals(1, storage.getAvailableProperties().size());
        Assert.assertEquals(MAX_ENTRY_TEST, storage.storageSize());

        storage.flush();
        System.out.println("Sparkey: Writing 10M Properties -- " + (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        IntStream.range(0, MAX_READING_TEST).forEach(x -> {
            try {
                int key = random.nextInt(MAX_ENTRY_TEST);
                String value = storage.get(String.valueOf(key), "RT");
            }catch (PgatkIOException ex){
                log.error("Error reading entry -- " + x);
            }
        });

        System.out.println("Sparkey: Reading 200'000 Properties -- " + (System.currentTimeMillis() - time) / 1000);

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

    @Test
    public void clusteringObjectDBTest() throws IOException {

        long time = System.currentTimeMillis();
        Random random = new Random();

        ObjectDBPropertyStorage storage = new ObjectDBPropertyStorage(new ObjectsDB(Files
                .createTempDirectory("properties-").toFile()
                .getAbsolutePath(), "properties-results.zpr")
        );
        Map<Long, Object> propertyBash = new HashMap<>();
        for(int i = 0; i < (100_000); i++){
            String key = i + "RT";
            SpectrumProperty property = new SpectrumProperty(key, String.valueOf(i), "RT", String.valueOf(Math.random()));
            propertyBash.put(LongObject.asLongHash(key), property);
            if((i+1) % 1000 == 0) {
                storage.addProperty(propertyBash);
                propertyBash.clear();
            }
        }

        Assert.assertEquals(100_000, storage.getNumber(SpectrumProperty.class));

        System.out.println("ObjectDB: Writing 100k Properties -- " + (System.currentTimeMillis() - time) / 1000);
        storage.flush();
        storage.close();

    }
}
