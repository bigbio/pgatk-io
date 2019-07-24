package org.bigbio.pgatk.io.clustering;

import junit.framework.Assert;
import org.bigbio.pgatk.io.common.cluster.ICluster;
import org.bigbio.pgatk.io.common.cluster.ISpectrumReference;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jg on 01.08.14.
 */
public class ClusteringFileReaderTest {
    public File testFile;
    public File completeFile;

    @Before
    public void setUp() throws URISyntaxException {
        URI testFileUri = ClusteringFileReaderTest.class.getClassLoader().getResource("testfile.clustering").toURI();
        URI completeSpectrumInfoFileUri = ClusteringFileReaderTest.class.getClassLoader().getResource("complete_spectrum_info.clustering").toURI();
        testFile = new File(testFileUri);
        completeFile = new File(completeSpectrumInfoFileUri);
    }

    @Test
    public void testReadClustersIteratively() throws Exception {
        IClusterSourceReader reader = new ClusteringFileReader(testFile);

        List<IClusterSourceListener> listeners = new ArrayList<>();
        reader.readClustersIteratively(listeners);
    }

    @Test
    public void testReadAllClusters() throws Exception {
        IClusterSourceReader reader = new ClusteringFileReader(testFile);

        List<ICluster> clusters = reader.readAllClusters();

        Assert.assertEquals(960, clusters.size());

        ICluster cluster = clusters.get(6);

        Assert.assertEquals(305.0, cluster.getPrecursorMZ());
        Assert.assertEquals(2, cluster.getSpecCount());

        Assert.assertEquals("PXD000090;PRIDE_Exp_Complete_Ac_27993.xml;spectrum=2338", cluster.getSpectrumReferences().get(0).getId());
        Assert.assertEquals(304.61032, cluster.getSpectrumReferences().get(0).getPrecursorMZ());
        Assert.assertEquals(0, cluster.getSpectrumReferences().get(0).getPrecursorCharge().intValue());

        ISpectrumReference ref = cluster.getSpectrumReferences().get(0);
        Assert.assertEquals(1, ref.getPSMs().size());
        Assert.assertFalse(ref.isIdentifiedAsMultiplePeptides());
        Assert.assertEquals("KGSCR", ref.getMostCommonPSM().getSequence());
        Assert.assertEquals(0, ref.getMostCommonPSM().getModifications().size());
    }


    @Test
    public void testReadCompleteSpectrumInformation() throws Exception {
        IClusterSourceReader reader = new ClusteringFileReader(completeFile);

        List<ICluster> clusters = reader.readAllClusters();

        Assert.assertEquals(1, clusters.size());

        ICluster cluster = clusters.get(0);
        List<ISpectrumReference> spectrumReferences = cluster.getSpectrumReferences();
        Assert.assertEquals(1, spectrumReferences.size());

        ISpectrumReference spectrum = spectrumReferences.get(0);
        Assert.assertEquals("PRD000715;PRIDE_Exp_Complete_Ac_24805.xml;spectrum=11", spectrum.getId());

        Assert.assertEquals(399.68015, spectrum.getPrecursorMZ());
        Assert.assertEquals(2, spectrum.getPrecursorCharge().intValue());
        Assert.assertEquals("9606", spectrum.getSpecies());
        Assert.assertEquals(1.0f, spectrum.getSimilarityScore());

        Assert.assertEquals(1, spectrum.getPSMs().size());
        Assert.assertFalse(spectrum.isIdentifiedAsMultiplePeptides());
        Assert.assertEquals("TSLAGGGR", spectrum.getMostCommonPSM().getSequence());
        Assert.assertEquals(1, spectrum.getMostCommonPSM().getModifications().size());
        Assert.assertEquals(1, spectrum.getMostCommonPSM().getModifications().get(0).getPosition());
        Assert.assertEquals("MOD:01455", spectrum.getMostCommonPSM().getModifications().get(0).getAccession());
    }

    @Test
    public void testReadConsensusSpectrum() throws Exception {
        IClusterSourceReader reader = new ClusteringFileReader(completeFile);

        List<ICluster> clusters = reader.readAllClusters();

        // mz and intens values are the same, count values are missing
        for (ICluster cluster : clusters) {
            int nMzValues = cluster.getConsensusMzValues().size();
            int nIntensValues = cluster.getConsensusIntensValues().size();
            int nCountsValues = cluster.getConsensusCountValues().size();

            Assert.assertTrue(nMzValues > 0);
            Assert.assertEquals(nMzValues, nIntensValues);
            Assert.assertEquals(0, nCountsValues);
        }
    }
}
