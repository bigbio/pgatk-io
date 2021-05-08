package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * {@link ExonInfo} is provided by the tool PepGenome, which maps peptides to genome coordinates
 *
 * @author ypriverol
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExonInfo{

    @JsonProperty("exonCount")
    private int exonCount;

    @JsonProperty("exonLengths")
    private int exonLengths;

    @JsonProperty("exonStarts")
    private int exonStarts;

    @JsonProperty("exonAccession")
    private String exonAccession;

    public ExonInfo() {
    }

    public ExonInfo(int exonCount, int exonLengths, int exonStarts, String exonAccession) {
        this.exonCount = exonCount;
        this.exonLengths = exonLengths;
        this.exonStarts = exonStarts;
        this.exonAccession = exonAccession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExonInfo exonInfo = (ExonInfo) o;

        if (exonStarts != exonInfo.exonStarts) return false;
        return exonAccession.equals(exonInfo.exonAccession);
    }

    @Override
    public int hashCode() {
        int result = exonStarts;
        result = 31 * result + exonAccession.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExonInfo{" +
                "exonCount=" + exonCount +
                ", exonLengths=" + exonLengths +
                ", exonStarts=" + exonStarts +
                ", exonAccession='" + exonAccession + '\'' +
                '}';
    }
}
