package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.io.msp.MspIterableReader;
import io.github.bigbio.pgatk.io.msp.MspSpectrum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrideJsonWriterTest {

  private File sourceFile;
  private MspIterableReader mspIterableReader;
  private File outpFile;
  private PrideJsonWriter prideJsonfile;

  @Before
  public void setup() throws PgatkIOException, URISyntaxException, IOException {
    URL testFile = getClass().getClassLoader().getResource("msp_test.msp");
    Assert.assertNotNull("Error loading msp test file", testFile);
    sourceFile = new File(testFile.toURI());
    mspIterableReader = new MspIterableReader(sourceFile);

    outpFile = File.createTempFile("temp", ".json");
    prideJsonfile = new PrideJsonWriter(outpFile);
  }

  @Test
  public void writeList() throws PgatkIOException {
    List<Spectrum> spectra = new ArrayList<>();
    while(mspIterableReader.hasNext()){
      spectra.add(mspIterableReader.next());
    }
    for(Spectrum spectrum: spectra){
      Double[] masses = new Double[spectrum.getPeakList().size()];
      Double[] intensities = new Double[spectrum.getPeakList().size()];
      MspSpectrum mspSpectrum = (MspSpectrum) spectrum;
      int index = 0;
      Iterator<Map.Entry<Double, Double>> it = spectrum.getPeakList().entrySet().iterator();
      while(it.hasNext()){
        Map.Entry<Double, Double> value = it.next();
        masses[index] = value.getKey();
        intensities[index] = value.getValue();
        index++;
      }

      ArchiveSpectrum archiveSpectrum = new ArchiveSpectrum(spectrum.getIndex().toString(), null, null, null, null,
        null, masses, intensities,spectrum.getPeakList().size(), 2, spectrum.getPrecursorCharge(), spectrum.getPrecursorMZ(), null,null, mspSpectrum.getPeptideSequence(),
        0, null,null,false, null, true);
      prideJsonfile.write(archiveSpectrum);
    }
    System.out.println("Write multiple spectra -- " + spectra.size());
    prideJsonfile.flush();
    prideJsonfile.close();
    outpFile.deleteOnExit();
  }
}
