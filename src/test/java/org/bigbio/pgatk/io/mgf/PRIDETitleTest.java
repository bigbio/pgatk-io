package org.bigbio.pgatk.io.mgf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

public class PRIDETitleTest {

    private MgfIndexedReader mgfFile = new MgfIndexedReader();

    @Before
    public void setUp() throws Exception {
        loadTestFile();
    }

    private void loadTestFile() throws Exception  {
        URL testFile = getClass().getClassLoader().getResource("pride_title.mgf");
        Assert.assertNotNull("Error loading mgf test file", testFile);
        File sourceFile = new File(testFile.toURI());
        mgfFile = new MgfIndexedReader(sourceFile, true, false);
    }

    @Test
    public void testGetTitle() throws Exception {
        Iterator<Ms2Query> it = mgfFile.getMs2QueryIterator();
        Assert.assertTrue("NULL SPECTRUM Encountered!", it.hasNext());
        //Checks First title
        Assert.assertEquals(it.next().getTitle(),"id=PXD000021;PRIDE_Exp_Complete_Ac_27179.xml;spectrum=0");
    }
}
