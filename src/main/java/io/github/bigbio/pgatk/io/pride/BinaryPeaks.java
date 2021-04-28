package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.utilities.spectra.SpectraUtilities;
import lombok.Builder;
import lombok.Data;
import java.util.List;


@Data
@Builder
/**
 * {@link BinaryPeaks} is a class to store the masses and intensities of an spectrum peaks in
 * the same class in binary. The class uses the utilities in {@link SpectraUtilities} to
 * compress and decompress the peak lists in to binary format.
 *
 * @author ypriverol
 */
public class BinaryPeaks {

  private byte[] binaryMasses;
  private byte[] binaryIntensities;

  public BinaryPeaks() {
  }

  public BinaryPeaks(byte[] binaryMasses, byte[] binaryIntensities) {
    this.binaryMasses = binaryMasses;
    this.binaryIntensities = binaryIntensities;
  }

  public BinaryPeaks(List<Double> masses, List<Double> intensities) {
    this.binaryMasses = SpectraUtilities.encodeBinary(masses);
    this.binaryIntensities = SpectraUtilities.encodeBinary(intensities);
  }
}
