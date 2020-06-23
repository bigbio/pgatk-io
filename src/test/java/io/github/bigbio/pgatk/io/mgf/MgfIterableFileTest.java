package io.github.bigbio.pgatk.io.mgf;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;

public class MgfIterableFileTest {

    private File sourceFile;
    private MgfIterableReader mgfIterableReader;

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception {
        URL testFile = getClass().getClassLoader().getResource("qExactive01819.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        sourceFile = new File(testFile.toURI());
        mgfIterableReader = new MgfIterableReader(sourceFile,true, false, true);
    }

    @Test
    public void next() {
        long time = System.currentTimeMillis();
        try {
            while (mgfIterableReader.hasNext()){
                Spectrum spectrum = mgfIterableReader.next();
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - time);
    }

    @Test
    public void performanceTime() throws PgatkIOException, URISyntaxException {
        long time = System.currentTimeMillis();
        URL testFile = getClass().getClassLoader().getResource("qExactive01819.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        sourceFile = new File(testFile.toURI());
        mgfIterableReader = new MgfIterableReader(sourceFile,true, false, true);
        int count = 0;
        while (mgfIterableReader.hasNext()){
            Ms2Query spectrum = (Ms2Query) mgfIterableReader.next();
            count++;
        }
        System.out.println("Spectra Read: " + count + " in Time " + (System.currentTimeMillis() - time));


    }
}