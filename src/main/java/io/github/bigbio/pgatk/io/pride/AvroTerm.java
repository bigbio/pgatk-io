package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.avro.reflect.Nullable;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvroTerm {

  @Nullable
  @JsonProperty("accession")
  private String accession;

  @JsonProperty("name")
  private String name;

  @JsonProperty("value")
  private String value;

  public AvroTerm() {
  }

  public AvroTerm(String accession, String name, String value) {
    this.accession = accession;
    this.name = name;
    this.value = value;
  }
}
