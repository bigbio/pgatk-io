package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

/**
 * {@link AccessionLocalization} is a class to store the position of a peptide in a protein sequence including the start, end and accession of the protein.
 *
 * @author ypriverol
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessionLocalization {

  // accession of the protein or gene
  @JsonProperty("accession")
  String accession;

  // start position
  @JsonProperty("start")
  long start;

  // end position
  @JsonProperty("end")
  long end;

  public AccessionLocalization() {
  }

  public AccessionLocalization(String accession, long start, long end) {
    this.accession = accession;
    this.start = start;
    this.end = end;
  }
}
