package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneCoordinates {

  @JsonProperty("start")
  private int start;

  @JsonProperty("end")
  private int end;

  @JsonProperty("transcriptUnique")
  private boolean transcriptUnique;

  @JsonProperty("geneUnique")
  private boolean geneUnique;

  @JsonProperty("chromosome")
  private String chromosome;

  @JsonProperty("transcriptAccession")
  private String transcriptAccession;

  @JsonProperty("geneAccession")
  private String geneAccession;

  @JsonProperty("geneName")
  private String geneName;

  @JsonProperty("geneType")
  private String geneType;

  @JsonProperty("strand")
  private String strand;

  @JsonProperty("exonInfoList")
  private List<ExonInfo> exonInfoList;

  public GeneCoordinates() {
  }

  public GeneCoordinates(int start, int end, boolean transcriptUnique, boolean geneUnique,
                         String chromosome, String transcriptAccession, String geneAccession,
                         String geneName, String geneType, String strand, List<ExonInfo> exonInfoList) {
    this.start = start;
    this.end = end;
    this.transcriptUnique = transcriptUnique;
    this.geneUnique = geneUnique;
    this.chromosome = chromosome;
    this.transcriptAccession = transcriptAccession;
    this.geneAccession = geneAccession;
    this.geneName = geneName;
    this.geneType = geneType;
    this.strand = strand;
    this.exonInfoList = exonInfoList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GeneCoordinates that = (GeneCoordinates) o;

    if (start != that.start) return false;
    if (end != that.end) return false;
    if (!Objects.equals(chromosome, that.chromosome)) return false;
    if (!Objects.equals(transcriptAccession, that.transcriptAccession))
      return false;
    return Objects.equals(geneAccession, that.geneAccession);
  }

  @Override
  public int hashCode() {
    int result = start;
    result = 31 * result + end;
    result = 31 * result + (chromosome != null ? chromosome.hashCode() : 0);
    result = 31 * result + (transcriptAccession != null ? transcriptAccession.hashCode() : 0);
    result = 31 * result + (geneAccession != null ? geneAccession.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GeneCoordinates{" +
      "exonInfoList=" + exonInfoList +
      ", start=" + start +
      ", end=" + end +
      ", transcriptUnique=" + transcriptUnique +
      ", geneUnique=" + geneUnique +
      ", chromosome='" + chromosome + '\'' +
      ", transcriptAccession='" + transcriptAccession + '\'' +
      ", geneAccession='" + geneAccession + '\'' +
      ", geneName='" + geneName + '\'' +
      ", geneType='" + geneType + '\'' +
      '}';
  }
}
