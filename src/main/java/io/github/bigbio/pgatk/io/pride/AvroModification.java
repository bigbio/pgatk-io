package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvroModification {

  @JsonProperty("properties")
  private List<AvroTerm> properties;

  @JsonProperty("modification")
  private String modification;

  @Nullable
  @JsonProperty("accession")
  private String accession;

  @JsonProperty("position")
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
