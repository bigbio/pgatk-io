package org.bigbio.pgatk.io.mgf;

import org.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.bigbio.pgatk.io.common.Spectrum;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class MgfIterableFileTest {

    private File sourceFile;
    private MgfIterableReader mgfIterableReader;

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception {
        URL testFile = getClass().getClassLoader().getResource("F001257.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        sourceFile = new File(testFile.toURI());
        mgfIterableReader = new MgfIterableReader(sourceFile,true, false, true);
    }

    @Test
    public void next() {
        try {
            while (mgfIterableReader.hasNext()){
                Spectrum spectrum = mgfIterableReader.next();
                System.out.println(spectrum);
            }

        } catch (PgatkIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void performanceTime() throws PgatkIOException, URISyntaxException {
        long time = System.currentTimeMillis();
        URL testFile = getClass().getClassLoader().getResource("F001257.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        sourceFile = new File(testFile.toURI());
        mgfIterableReader = new MgfIterableReader(sourceFile,true, false, true);
        while (mgfIterableReader.hasNext()){
            Spectrum spectrum = mgfIterableReader.next();
            System.out.println(spectrum.getId());
        }
        System.out.println(System.currentTimeMillis() - time);


    }
}