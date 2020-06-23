package io.github.bigbio.pgatk.io.common.cluster;

import io.github.bigbio.pgatk.io.common.psms.SequenceCount;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jg on 10.07.14.
 */
public interface ICluster extends Spectrum {


    /**
     * Get Peptide Sequences for the cluster
     * @return Peptide Sequence
     */
    Set<String> getSequences();

    /**
     * Get List of Spectrum References
     * @return List of {@link ISpectrumReference}
     */
    List<ISpectrumReference> getSpectrumReferences();

    /**
     * Get Spectrum Count
     * @return
     */
    int getSpecCount();

    /**
     * Get the Identified Spectrum Count
     * @return
     */
    int getIdentifiedSpecCount();

    /**
     * Get Unidentified Spectrum Count
     * @return
     */
    int getUnidentifiedSpecCount();

    /**
     * Returns the total number of PSMs. This can be larger than the total
     * number of spectra as spectra can be identified as multiple peptides.
     * @return
     */
    int getPsmCount();

    /**
     * Get number of SequenceCounts.
     * @return
     */
    List<SequenceCount> getSequenceCounts();

    /**
     * Returns a Map with the PSM sequence as key and its occurrence
     * as value.
     * @return
     */
    Map<String, Integer> getPsmSequenceCounts();

    /**
     * Get Max Ratio
     * @return
     */
    double getMaxRatio();

    /**
     * Get Max Sequence
     * @return
     */
    String getMaxSequence();

    /**
     * Get precursor MzRange
     * @return
     */
    double getSpectrumPrecursorMzRange();

    /**
     * Get MzValues as a List
     * @return
     */
    List<Double> getConsensusMzValues();

    /**
     * Get Consensus Intensity values as List.
     * @return
     */
    List<Double> getConsensusIntensValues();

    /**
     * Get number of spectrum that contains the specific peak.
     * @return
     */
    List<Integer> getConsensusCountValues();


    Set<String> getSpecies();
}
