package io.github.bigbio.pgatk.io.common.cluster;

import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.psms.IPeptideSpectrumMatch;
import io.github.bigbio.pgatk.io.common.psms.SequenceCount;

import java.util.*;


public class ClusteringFileCluster implements ICluster {

    private final double avPrecursorMz;
    private final double avPrecursorIntens;

    private final List<SequenceCount> sequenceCounts;
    private final List<ISpectrumReference> spectrumRefs;
    private final String maxSequence;
    private Double minSpecPrecursorMz = Double.MAX_VALUE;
    private Double maxSpecPrecursorMz = 0.0;

    private final float maxRatio;
    private final int totalPsms;

    private final List<Double> consensusMzValues;
    private final List<Double> consensusIntensValues;
    private final List<Integer> consensusCountValues;

    private final String id;

    private final int identifiedSpecCount;
    private final int unidentifiedSpecCount;

    private Map<String, Integer> countPerPsmSequence;

    private Set<String> species;

    private final String fileName;

    @Override
    public String getMaxSequence() {
        return maxSequence;
    }

    public ClusteringFileCluster(double avPrecursorMz,
                                 double avPrecursorIntens,
                                 List<ISpectrumReference> spectrumRefs,
                                 List<Double> consensusMzValues,
                                 List<Double> consensusIntensValues,
                                 List<Integer> consensusCountValues,
                                 String id,
                                 String fileName) {
        this.avPrecursorMz = avPrecursorMz;
        this.avPrecursorIntens = avPrecursorIntens;
        this.spectrumRefs = spectrumRefs;
        this.consensusMzValues = consensusMzValues;
        this.consensusIntensValues = consensusIntensValues;
        this.consensusCountValues = consensusCountValues;
        this.id = id;
        this.fileName = fileName;

        // calculate the ratio for each sequence
        int nTotalPSMs = 0, nIdentifiedSpec = 0, nUnidentifiedSpec = 0;
        countPerPsmSequence = new HashMap<>();

        for (ISpectrumReference specRef : spectrumRefs) {
            // SpecRefs now only store unqiue psms, the previous HashSet is no longer necessary
            for (IPeptideSpectrumMatch psm : specRef.getPSMs()) {
                if (!countPerPsmSequence.containsKey(psm.getSequence()))
                    countPerPsmSequence.put(psm.getSequence(), 0);

                countPerPsmSequence.put(psm.getSequence(), countPerPsmSequence.get(psm.getSequence()) + 1);

                nTotalPSMs++;
            }

            if (specRef.isIdentified())
                nIdentifiedSpec++;
            else
                nUnidentifiedSpec++;
        }

        totalPsms = nTotalPSMs;
        this.identifiedSpecCount = nIdentifiedSpec;
        this.unidentifiedSpecCount = nUnidentifiedSpec;

        // create the sequence counts
        this.sequenceCounts = new ArrayList<>();
        for (String s : countPerPsmSequence.keySet()) {
            SequenceCount sc = new SequenceCount(s, countPerPsmSequence.get(s));
            sequenceCounts.add(sc);
        }

        // get the maximum sequence
        String tmpMaxSequence = null;
        int maxSequenceCount = 0;

        for (String s : countPerPsmSequence.keySet()) {
            if (countPerPsmSequence.get(s) > maxSequenceCount) {
                tmpMaxSequence = s;
                maxSequenceCount = countPerPsmSequence.get(s);
            }
        }

        float ratio = (float) maxSequenceCount / (float) identifiedSpecCount;
        if (ratio > 1) {
            maxRatio = 1.0f;
        } else if (identifiedSpecCount < 1) {
            maxRatio = 0;
        }
        else {
            maxRatio = ratio;
        }

        maxSequence = tmpMaxSequence;

        // get min and max spec precursor mz
        for (ISpectrumReference specRef : spectrumRefs) {
            double specPrecursorMz = specRef.getPrecursorMZ();

            if (minSpecPrecursorMz > specPrecursorMz)
                minSpecPrecursorMz = specPrecursorMz;
            if (maxSpecPrecursorMz < specPrecursorMz)
                maxSpecPrecursorMz = specPrecursorMz;
        }

        // save the species
        species = new HashSet<>();
        for (ISpectrumReference specRef : spectrumRefs) {
            if (specRef.getSpecies() == null) {
                continue;
            }
            species.add(specRef.getSpecies());
        }
    }

    @Override
    public Set<String> getSequences() {
        return Collections.unmodifiableSet(countPerPsmSequence.keySet());
    }

    @Override
    public int getSpecCount() {
        return spectrumRefs.size();
    }

    @Override
    public double getMaxRatio() {
        return maxRatio;
    }

    @Override
    public List<SequenceCount> getSequenceCounts() {
        return Collections.unmodifiableList(sequenceCounts);
    }

    @Override
    public double getSpectrumPrecursorMzRange() {
        return maxSpecPrecursorMz - minSpecPrecursorMz;
    }

    @Override
    public List<ISpectrumReference> getSpectrumReferences() {
        return Collections.unmodifiableList(spectrumRefs);
    }

    @Override
    public List<Double> getConsensusMzValues() {
        return Collections.unmodifiableList(consensusMzValues);
    }

    @Override
    public List<Double> getConsensusIntensValues() {
        return Collections.unmodifiableList(consensusIntensValues);
    }

    @Override
    public List<Integer> getConsensusCountValues() {
        return Collections.unmodifiableList(consensusCountValues);
    }

    @Override
    public Long getIndex() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getPrecursorCharge() {
        return null;
    }

    @Override
    public Double getPrecursorMZ() {
        return avPrecursorMz;
    }

    @Override
    public Double getPrecursorIntensity() {
        return avPrecursorIntens;
    }

    @Override
    public Map<Double, Double> getPeakList() {
        Map<Double, Double> peaks = new HashMap<>();
        for(int i = 0; i < consensusMzValues.size(); i++)
            peaks.put(consensusMzValues.get(i), consensusIntensValues.get(i));
        return peaks;
    }

    @Override
    public Integer getMsLevel() {
        return 2;
    }

    @Override
    public Collection<? extends Param> getAdditional() {
        return null;
    }

    @Override
    public int getPsmCount() {
        return totalPsms;
    }

    @Override
    public Map<String, Integer> getPsmSequenceCounts() {
        return Collections.unmodifiableMap(countPerPsmSequence);
    }

    @Override
    public Set<String> getSpecies() {
        return Collections.unmodifiableSet(species);
    }

    @Override
    public int getIdentifiedSpecCount() {
        return identifiedSpecCount;
    }

    @Override
    public int getUnidentifiedSpecCount() {
        return unidentifiedSpecCount;
    }

    public String getFileName() {
        return fileName;
    }
}
