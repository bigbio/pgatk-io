package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;
import org.apache.avro.reflect.Nullable;

@Data
@Builder
public class AvroTerm {

  @Nullable
  private String accession;
  private String name;
  private String value;

  public AvroTerm() {
  }

  public AvroTerm(String accession, String name, String value) {
    this.accession = accession;
    this.name = name;
    this.value = value;
  }
}
