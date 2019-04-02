package org.bigbio.pgatk.io.mzml;

import org.bigbio.pgatk.io.common.IndexElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.bigbio.pgatk.io.common.Spectrum;

import java.io.File;
import java.net.URL;
import java.util.List;

public class MzMlTest {

    private static MzMlIndexedReader wrapper;
    private static File sourcefile;

    @Before
    public void setUp() throws Exception {
        if (sourcefile != null)
            return;

        URL testFile = getClass().getClassLoader().getResource("dta_example.mzML");
        Assert.assertNotNull("Error loading mzData test file", testFile);

        try {
            sourcefile = new File(testFile.toURI());

            Assert.assertNotNull(sourcefile);

            wrapper = new MzMlIndexedReader(sourcefile);

            Assert.assertNotNull(wrapper);
        } catch (Exception e) {
            System.out.println("Faild to load test file");
        }
    }

    @Test
    public void testSpectraLoading() {
        Assert.assertEquals(10, wrapper.getSpectraCount());
        Assert.assertTrue(wrapper.acceptsFile());
        Assert.assertFalse(wrapper.acceptsDirectory());


        try {
            int count = 0;

            while (wrapper.hasNext()) {
                Spectrum s = wrapper.next();
                Assert.assertNotNull(s);
                count++;
            }

            Assert.assertEquals(10, count);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSpecByIndex() {
        try {
            Spectrum s = wrapper.getSpectrumByIndex(1);
            Assert.assertEquals("scan=3", s.getId());
            Assert.assertEquals(1, s.getPrecursorCharge().intValue());
            Assert.assertEquals(419.115, s.getPrecursorMZ(), 0.0);

            Assert.assertEquals(92, s.getPeakList().size());

            Assert.assertEquals(5876118.0, s.getPeakList().get(419.0830078125), 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSpecById() {
        try {
            Spectrum s = wrapper.getSpectrumById("scan=3");
            Assert.assertEquals("scan=3", s.getId());
            Assert.assertEquals(1, s.getPrecursorCharge().intValue());
            Assert.assertEquals(419.115, s.getPrecursorMZ(), 0.0);

            Assert.assertEquals(92, s.getPeakList().size());

            Assert.assertEquals(5876118.0, s.getPeakList().get(419.0830078125), 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetSpectrumByMSLevel() {
        try {

            List<Integer> mslevels = wrapper.getMsLevels();
            Assert.assertTrue("MS Level 2 not found", mslevels.contains(2));

            List<IndexElement> index = wrapper.getMsNIndexes(2);
            Assert.assertEquals("Incorrect number of MS2 spectra", 10, index.size());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


}
