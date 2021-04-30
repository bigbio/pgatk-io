package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;
import org.apache.avro.reflect.Nullable;

import java.util.List;

@Builder
@Data
/**
 * The parquet modification is shorter in size than other representations. Each modification is
 * represent by:
 *
 * - modification: modification name from UNIMOD.
 * - accession: Accession of the modification
 * - positionMap: A map with the position and corresponding scores for that position.
 */
public class AvroModification {

  private List<AvroTerm> properties;
  private String modification;
  @Nullable
  private String accession;
  private Integer position;

  public AvroModification() {
  }

  public AvroModification(List<AvroTerm> properties, String modification, String accession, Integer position) {
    this.properties = properties;
    this.modification = modification;
    this.accession = accession;
    this.position = position;
  }
}
