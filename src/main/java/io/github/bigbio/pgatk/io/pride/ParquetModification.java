package io.github.bigbio.pgatk.io.pride;

import io.github.bigbio.pgatk.io.utils.Tuple;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
/**
 * The parquet modification is shorter in size than other representations. Each modification is
 * represent by:
 *
 * - modification: modification name from UNIMOD.
 * - positionMap: A map with the position and corresponding scores for that position.

 */
public class ParquetModification {

  private List<ParquetTuple> properties;
  private String modification;
  private Integer position;

  public ParquetModification() {
  }

  public ParquetModification(List<ParquetTuple> properties, String modification, Integer position) {
    this.properties = properties;
    this.modification = modification;
    this.position = position;
  }

}
