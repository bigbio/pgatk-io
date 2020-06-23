package io.github.bigbio.pgatk.io.mgf;

import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.net.URL;

/**
 * Get custom tags from the MGF.
 */
public class CustomTagTest{

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception{
        URL testFile = getClass().getClassLoader().getResource("custom_tags.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        File sourceFile;
        MgfIndexedReader mgfFile = new MgfIndexedReader();
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
