package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.utilities.spectra.SpectraUtilities;
import lombok.Data;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CV;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.params.BinaryDataArrayCVParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Data
public class BinaryPeaks {

  private String binaryMasses;
  private String binaryIntensities;

  public BinaryPeaks() {
  }

  public BinaryPeaks(String binaryMasses, String binaryIntensities) {
    this.binaryMasses = binaryMasses;
    this.binaryIntensities = binaryIntensities;
  }

  public BinaryPeaks(List<Double> masses, List<Double> intensities) {
    this.binaryMasses = SpectraUtilities.encodeBinary(masses);
    this.binaryIntensities = SpectraUtilities.encodeBinary(intensities);
  }
}
