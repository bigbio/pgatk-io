package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

/**
 * {@link AccessionLocalization} is a class to store the position of a peptide in a protein sequence including the start, end and accession of the protein.
 *
 * @author ypriverol
 */
public class AccessionLocalization {

  // accession of the protein or gene
  String accession;

  // start position
  long start;

  // end position
  long end;

  public AccessionLocalization() {
  }

  public AccessionLocalization(String accession, long start, long end) {
    this.accession = accession;
    this.start = start;
    this.end = end;
  }
}
