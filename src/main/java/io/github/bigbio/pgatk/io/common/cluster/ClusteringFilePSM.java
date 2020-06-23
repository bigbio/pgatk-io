package io.github.bigbio.pgatk.io.common.cluster;

import io.github.bigbio.pgatk.io.common.modification.IModification;
import io.github.bigbio.pgatk.io.common.psms.IPeptideSpectrumMatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jg on 24.09.14.
 */
public class ClusteringFilePSM implements IPeptideSpectrumMatch {

    private final String sequence;
    private List<IModification> modifications;

    public ClusteringFilePSM(String sequence) {
        this.sequence = sequence;
        modifications = new ArrayList<>();
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public List<IModification> getModifications() {
        return Collections.unmodifiableList(modifications);
    }

    public void addModification(IModification modification) {
        modifications.add(modification);
    }

    public void addModifications(Collection<IModification> modifications) {
        this.modifications.addAll(modifications);
    }

    public void clearModifications() {
        modifications.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusteringFilePSM that = (ClusteringFilePSM) o;

        if (!sequence.equals(that.sequence)) return false;
        return modifications.equals(that.modifications);
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + modifications.hashCode();
        return result;
    }
}
