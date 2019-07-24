package org.bigbio.pgatk.io.clustering.objects;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jg on 10.07.14.
 */
public interface ICluster {

    float getAvPrecursorMz();

    float getAvPrecursorIntens();

    Set<String> getSequences();

    List<ISpectrumReference> getSpectrumReferences();

    int getSpecCount();

    int getIdentifiedSpecCount();

    int getUnidentifiedSpecCount();

    /**
     * Returns the total number of PSMs. This can be larger than the total
     * number of spectra as spectra can be identified as multiple peptides.
     * @return
     */
    int getPsmCount();

    List<SequenceCount> getSequenceCounts();

    /**
     * Returns a Map with the PSM sequence as key and its occurrence
     * as value.
     * @return
     */
    Map<String, Integer> getPsmSequenceCounts();

    float getMaxRatio();

    String getMaxSequence();

    float getSpectrumPrecursorMzRange();

    List<Float> getConsensusMzValues();

    List<Float> getConsensusIntensValues();

    List<Integer> getConsensusCountValues();

    String getId();

    Set<String> getSpecies();
}
