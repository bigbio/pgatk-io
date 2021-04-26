package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AnnotatedBinaryPeaksJsonTest {

  AnnotatedSpectrum spec;

  @Before
  public void setup(){

    List<Double> masses = Arrays.asList(2.3,3.4,4.5);
    List<Double> intensities = Arrays.asList(100.7,100.8,100.9);

    spec = new AnnotatedSpectrum("USI1", "KKKKR", "[Acetyl]-KKKKR",
      (Set<String>) Collections.singleton("Protein1"), null, null, null, "Sample1", "human", Collections.EMPTY_LIST,
      Collections.EMPTY_LIST, 12345.67, 2, Collections.EMPTY_LIST,masses, intensities, 23.5, 2, 1, Collections.EMPTY_SET,
      Collections.EMPTY_LIST, "PXD2303030", true, 1234567.890345);
  }

  @Test
  public void binaryTest() throws PgatkIOException {
    File outpFile = new File("binarySpectrum.json");
    PrideJsonWriter prideJsonfile = new PrideJsonWriter(outpFile);
    prideJsonfile.write(spec);
    prideJsonfile.flush();
    prideJsonfile.close();

    outpFile = new File("binarySpectrum.json");
    PrideJsonIterableReader reader = new PrideJsonIterableReader(outpFile, AnnotatedSpectrum.class);
    while(reader.hasNext()){
      AnnotatedSpectrum readSpec = (AnnotatedSpectrum) reader.next();
      System.out.println(readSpec.toString());
      System.out.println(readSpec.getPeakList().toString());
    }


    reader.close();
    outpFile.deleteOnExit();
  }



}
