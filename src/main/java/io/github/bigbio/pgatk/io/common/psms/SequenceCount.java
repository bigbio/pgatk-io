package io.github.bigbio.pgatk.io.common.psms;

/**
 * Created by jg on 10.07.14.
 */
public class SequenceCount {

    /**
     * Peptide Sequence
     */
    private final String sequence;

    /**
     * Number of occurrences of the sequence in the cluster.
     */
    private final int count;

    public SequenceCount(String sequence, int count) {
        this.sequence = sequence;
        this.count = count;
    }

    public String getSequence() {
        return sequence;
    }

    public int getCount() {
        return count;
    }
}
