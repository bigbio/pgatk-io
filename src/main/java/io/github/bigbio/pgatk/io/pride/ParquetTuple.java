package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParquetTuple {

  String key;
  String value;

  public ParquetTuple() {
  }

  public ParquetTuple(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
