package org.bigbio.pgatk.io.clustering.objects;

/**
 * Created by jg on 10.07.14.
 */
public class SequenceCount {
    private final String sequence;
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
