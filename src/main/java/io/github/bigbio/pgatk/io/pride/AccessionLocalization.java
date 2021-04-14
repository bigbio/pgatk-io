package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessionLocalization {

  // accession of the protein or gene
  String accession;

  // start position
  long start;

  // end position
  long end;
}
