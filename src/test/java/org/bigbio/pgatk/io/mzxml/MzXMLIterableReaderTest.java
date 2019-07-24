package org.bigbio.pgatk.io.mzxml;

import org.bigbio.pgatk.io.common.spectra.Spectrum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class MzXMLIterableReaderTest {


    private static MzXMLIterableReader mzxmlIndexedReader;
    private File sourcefile;

    @Before
    public void setUp() {
        // create the mzxml dao
        try {
            URL testFile = getClass().getClassLoader().getResource("testfile.mzXML");
            Assert.assertNotNull("Error loading mzXML test file", testFile);
            sourcefile = new File(testFile.toURI());

            if (mzxmlIndexedReader == null)
                mzxmlIndexedReader = new MzXMLIterableReader(sourcefile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void next() {

        int count = 0;
        while(mzxmlIndexedReader.hasNext()){
            Spectrum spectrum = mzxmlIndexedReader.next();
            count++;
        }
        Assert.assertEquals(3314, count);
    }
}