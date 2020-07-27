package io.github.bigbio.pgatk.io.msp;

import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.io.mgf.MgfIterableReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class MspIterableReaderTest {

  private File sourceFile;
  private MspIterableReader mspIterableReader;

  @Before
  public void setUp() throws Exception {
    loadTestFile();
  }

  private void loadTestFile() throws Exception {
    URL testFile = getClass().getClassLoader().getResource("msp_test.msp");
    Assert.assertNotNull("Error loading msp test file", testFile);
    sourceFile = new File(testFile.toURI());
    mspIterableReader = new MspIterableReader(sourceFile);
  }

  @Test
  public void hasNext() {
    while(mspIterableReader.hasNext()){
      Spectrum spectrum = mspIterableReader.next();
      System.out.println(spectrum.toString());
    }
  }
}
