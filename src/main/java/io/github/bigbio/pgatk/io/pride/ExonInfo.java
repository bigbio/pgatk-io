package io.github.bigbio.pgatk.io.pride;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExonInfo{

    private int exonCount;
    private int exonLengths;
    private int exonStarts;
    private String exonAccession;

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
