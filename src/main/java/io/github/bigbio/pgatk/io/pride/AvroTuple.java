package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvroTuple {

  String key;
  String value;

  public AvroTuple() {
  }

  public AvroTuple(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
