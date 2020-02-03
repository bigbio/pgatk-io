package org.bigbio.pgatk.io.clustering.indexing;

import junit.framework.Assert;
import org.bigbio.pgatk.io.braf.BufferedRandomAccessFile;
import org.bigbio.pgatk.io.clustering.ClusteringFileReader;
import org.bigbio.pgatk.io.common.cluster.ICluster;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * @author ypriverol
 * @author jgriss
 */
public class ClusteringFileIndexerTest {
    private File oldTestFile;
    private File newTestFile;

    @Before
    public void setUp() throws Exception {
        oldTestFile = new File(ClusteringFileIndexerTest.class.getClassLoader().getResource("testfile.clustering").toURI());
        newTestFile = new File(ClusteringFileIndexerTest.class.getClassLoader().getResource("pride_export_17.clustering").toURI());
    }

    @Test
    public void testClusteringIndexer() throws Exception {
        IIndexer indexer = new ClusteringFileIndexer();
        ClusteringFileIndex fileIndex = indexer.indexFile(oldTestFile);
        Map<String, ClusteringIndexElement> index = fileIndex.getIndex();

        Assert.assertEquals("Incorrect number of clusters found",960, index.size());
        Assert.assertTrue(index.containsKey("0"));
        Assert.assertTrue(index.containsKey("47"));

        ClusteringIndexElement indexElement = index.get("0");
        Assert.assertEquals("Wrong first cluster offset", 151, indexElement.getStart());
    }

    @Test
    public void testClusteringIndexerExport17() throws Exception {
        IIndexer indexer = new ClusteringFileIndexer();
        Map<String, ClusteringIndexElement> index = indexer.indexFile(newTestFile).getIndex();

        Assert.assertEquals("Incorrect number of clusters found",107, index.size());
        Assert.assertTrue("c8ada97f-094d-409b-8651-3d2efc77dbea", index.containsKey("c8ada97f-094d-409b-8651-3d2efc77dbea"));
        Assert.assertTrue("Missing last cluster", index.containsKey("08b511d4-25f7-4630-9d44-04c6fee124a5"));

        ClusteringIndexElement indexElement = index.get("c8ada97f-094d-409b-8651-3d2efc77dbea");
        Assert.assertEquals(152, indexElement.getStart());

        // load the string
        BufferedRandomAccessFile accessFile = new BufferedRandomAccessFile(newTestFile, "r", 1024 * 100);
        byte[] byteBuffer = new byte[indexElement.getSize()];
        accessFile.seek(indexElement.getStart());
        accessFile.read(byteBuffer);
        String clusterString = new String(byteBuffer);

        Assert.assertTrue(clusterString.startsWith("=Cluster="));
        Assert.assertTrue(clusterString.endsWith("combined_fdr_score=64.00369022128015\"}\n"));

        Assert.assertEquals(828, indexElement.getSize());
    }

    @Test
    public void testRandomAccess() throws Exception {
        IIndexer indexer = new ClusteringFileIndexer();
        ClusteringFileIndex index = indexer.indexFile(newTestFile);

        ClusteringFileReader reader = new ClusteringFileReader(newTestFile, index);

        ICluster cluster = reader.readCluster("c8ada97f-094d-409b-8651-3d2efc77dbea");

        Assert.assertNotNull(cluster);
        Assert.assertEquals(1, cluster.getSpecCount());
        Assert.assertEquals(1, cluster.getPsmCount());
        Assert.assertEquals(400.0, cluster.getPrecursorMZ());
        Assert.assertEquals("GLPFILILLAK", cluster.getMaxSequence());
        Assert.assertEquals(0, cluster.getUnidentifiedSpecCount());

        ICluster cluster2 = reader.readCluster("e075098d-875e-4359-b71b-35ebcf124a02");
        Assert.assertNotNull(cluster2);
        Assert.assertEquals(400.0039978027344, cluster2.getPrecursorMZ());
        Assert.assertEquals(4, cluster2.getSpecCount());
        Assert.assertEquals(0, cluster.getUnidentifiedSpecCount());
    }

    @Test
    public void testBinaryReadWrite() throws Exception {
        IIndexer indexer = new ClusteringFileIndexer();
        ClusteringFileIndex index = indexer.indexFile(newTestFile);

        File temporaryFile = File.createTempFile("index_test", ".index");
        index.saveToFile(temporaryFile);

        ClusteringFileIndex loadedIndex = ClusteringFileIndex.loadFromFile(temporaryFile);

        Assert.assertEquals("Incorrect number of clusters found",107, loadedIndex.getIndex().size());
        Assert.assertTrue("c8ada97f-094d-409b-8651-3d2efc77dbea", loadedIndex.getIndex().containsKey("c8ada97f-094d-409b-8651-3d2efc77dbea"));
        Assert.assertTrue("Missing last cluster", loadedIndex.getIndex().containsKey("08b511d4-25f7-4630-9d44-04c6fee124a5"));

        ClusteringIndexElement indexElement = loadedIndex.getIndex().get("c8ada97f-094d-409b-8651-3d2efc77dbea");
        Assert.assertEquals(152, indexElement.getStart());

        temporaryFile.delete();
    }
}
