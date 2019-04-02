package org.bigbio.pgatk.io.mzxml;

import org.bigbio.pgatk.io.common.Spectrum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class MzXMLIterableReaderTest {


    private static MzXMLIterableReader mzxmlIndexedReader;
    private File sourcefile;

    @Before
    public void setUp() throws Exception {
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

        while(mzxmlIndexedReader.hasNext()){
            Spectrum spectrum = mzxmlIndexedReader.next();
            System.out.println(spectrum.getId() + " Number of peaks: " + spectrum.getPeakList().size());
        }
    }
}