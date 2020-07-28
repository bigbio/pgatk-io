package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.apl.TestAplIndexedReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class PrideJsonIndexedReaderTest {

  private PrideJsonIndexedReader prideJsonIndexedReader;
  private File sourceFile;

  @Before
  public  void setUp() {
    URL testFile = TestAplIndexedReader.class.getClassLoader()
      .getResource("PXD015890_114263_ArchiveSpectrum.json");
    Assert.assertNotNull("Error loading apl test file", testFile);

    try {
      sourceFile = new File(testFile.toURI());
      prideJsonIndexedReader = new PrideJsonIndexedReader(sourceFile, true);
    } catch (Exception e) {
      System.out.println("Faild to load test file");
    }
  }


  @Test
  public void getSpectraCount() {
    Assert.assertTrue(prideJsonIndexedReader.getSpectraCount() == 7824);
  }

  @Test
  public void getSpectraIds() {
  }

  @Test
  public void getSpectrumById() {
  }
}
