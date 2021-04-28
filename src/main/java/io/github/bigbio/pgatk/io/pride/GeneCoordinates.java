package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class GeneCoordinates {

  private int start;
  private int end;
  private boolean transcriptUnique;
  private boolean geneUnique;
  private String chromosome;
  private String transcriptAccession;

  private String geneAccession;
  private String geneName;
  private String geneType;

  private List<ExonInfo> exonInfoList;

  public GeneCoordinates() {
  }

  public GeneCoordinates(int start, int end, boolean transcriptUnique, boolean geneUnique,
                         String chromosome, String transcriptAccession, String geneAccession,
                         String geneName, String geneType, List<ExonInfo> exonInfoList) {
    this.start = start;
    this.end = end;
    this.transcriptUnique = transcriptUnique;
    this.geneUnique = geneUnique;
    this.chromosome = chromosome;
    this.transcriptAccession = transcriptAccession;
    this.geneAccession = geneAccession;
    this.geneName = geneName;
    this.geneType = geneType;
    this.exonInfoList = exonInfoList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GeneCoordinates that = (GeneCoordinates) o;

    if (start != that.start) return false;
    if (end != that.end) return false;
    if (chromosome != null ? !chromosome.equals(that.chromosome) : that.chromosome != null) return false;
    if (transcriptAccession != null ? !transcriptAccession.equals(that.transcriptAccession) : that.transcriptAccession != null)
      return false;
    return geneAccession != null ? geneAccession.equals(that.geneAccession) : that.geneAccession == null;
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
