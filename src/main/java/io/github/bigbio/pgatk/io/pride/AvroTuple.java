package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvroTuple {

  @JsonProperty("key")
  String key;

  @JsonProperty("value")
  String value;

  public AvroTuple() {
  }

  public AvroTuple(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
