package org.bigbio.pgatk.io.mgf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

/**
 * Get custom tags from the MGF.
 */
public class CustomTagTest{

    private MgfIndexedReader mgfFile = new MgfIndexedReader();

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception{
        URL testFile = getClass().getClassLoader().getResource("custom_tags.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        File sourceFile;
        try {
            sourceFile = new File(testFile.toURI());
            mgfFile = new MgfIndexedReader(sourceFile, false, false);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Unknown attribute '_DISTILLER_MDRO_VERSION' encountered");
        }
        sourceFile = new File(testFile.toURI());
        mgfFile = new MgfIndexedReader(sourceFile, true, false);
    }

}
