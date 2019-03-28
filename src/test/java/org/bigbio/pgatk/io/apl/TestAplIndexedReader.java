package org.bigbio.pgatk.io.apl;

import org.bigbio.pgatk.io.common.IndexElement;
import org.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.bigbio.pgatk.io.common.Spectrum;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TestAplIndexedReader {

    private AplIndexedReader aplIndexedReader;
    private File sourceFile;

    @Before
    public  void setUp() {
        URL testFile = TestAplIndexedReader.class.getClassLoader().getResource("allSpectra.CID.ITMS.sil0.apl");
        Assert.assertNotNull("Error loading apl test file", testFile);

        try {
            sourceFile = new File(testFile.toURI());

            aplIndexedReader = new AplIndexedReader(sourceFile);
        } catch (Exception e) {
            System.out.println("Faild to load test file");
        }
    }

    @Test
    public void testGetFormat() {
        Assert.assertEquals("Andromeda peaklist file", aplIndexedReader.getFormat());
    }

    @Test
    public void testSetPeakLists() {
        AplSpectrum query;
        try {
            query = new AplSpectrum("peaklist start\nmz=1271.13935636076\ncharge=4\nheader=RawFile: 20080830_Orbi6_NaNa_SA_BiotechVariation_MH03_02 Index: 16236 Silind: 43885\n380.02725\t7.750772\n419.57687\t11.58331\n423.23862\t10.6417\npeaklist end\n", (long) 1);

            ArrayList<AplSpectrum> queries = new ArrayList<>();
            queries.add(query);

            aplIndexedReader.setPeakLists(queries);

            Assert.assertEquals("peaklist start\nmz=1271.13935636076\ncharge=4\nheader=RawFile: 20080830_Orbi6_NaNa_SA_BiotechVariation_MH03_02 Index: 16236 Silind: 43885\n380.02725\t7.750772\n419.57687\t11.58331\n423.23862\t10.6417\npeaklist end\n\n", aplIndexedReader.toString());
        } catch (PgatkIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetPeakListCount() {
        Assert.assertEquals(10, aplIndexedReader.getPeakListCount());
    }

    @Test
    public void testGetPeakList() {
        try {
            Assert.assertNotNull(aplIndexedReader.getPeakList(7));
        } catch (PgatkIOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testAplFile() {
        // get the index
        List<IndexElement> index = aplIndexedReader.getIndex();

        // create the new file
        AplIndexedReader newFile;
        try {
            newFile = new AplIndexedReader(sourceFile, index);

            while (aplIndexedReader.hasNext() && newFile.hasNext()) {
                Assert.assertEquals(aplIndexedReader.next().toString(), newFile.next().toString());
            }
        } catch (PgatkIOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetIndex() {
        try {
            List<IndexElement> index = aplIndexedReader.getMsNIndexes(2);

            Spectrum s = aplIndexedReader.getSpectrumByIndex(3);

            Spectrum s1 = AplIndexedReader.getIndexedSpectrum(sourceFile, index.get(2));

            Assert.assertEquals(s.toString(), s1.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
