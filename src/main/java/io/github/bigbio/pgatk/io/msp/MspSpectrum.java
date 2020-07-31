package io.github.bigbio.pgatk.io.msp;

import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.io.utils.Tuple;

import java.util.*;

public class MspSpectrum implements Spectrum {
    private long index;
    Map<Double, Double> peakList;
    private static final int DEFAULT_NUMBER_PEAKS = 100;
    private Integer msLevel;
    private Double precursorIntensity;
    private Double precursorMz;
    private Integer precursorCharge;
    private String usi;

    private String peptideSequence;

    private List<Tuple<Integer, String>> ptms;
    private Map<String, String> properties;
    private Map<String, Double> scores;

    private List<String> proteins;

    /**
     * Default Constructor
     */
    public MspSpectrum(){ }

    public MspSpectrum(String peptideSequence, Integer charge) {
        this.precursorCharge = charge;
        this.peptideSequence = peptideSequence;
    }

    @Override
    public Long getIndex() {
        return index;
    }

    @Override
    public String getId() {
        return usi;
    }

    @Override
    public Integer getPrecursorCharge() {
        return precursorCharge;
    }

    @Override
    public Double getPrecursorMZ() {
        return precursorMz;
    }

    @Override
    public Double getPrecursorIntensity() {
        return precursorIntensity;
    }

    @Override
    public Map<Double, Double> getPeakList() {
        return peakList;
    }

    @Override
    public Integer getMsLevel() {
        return this.msLevel;
    }

    @Override
    public Collection<? extends Param> getAdditional() {
        return Collections.emptyList();
    }

    public void setIndex(long specIndex) {
        this.index = specIndex;
    }

    public void addPeak(double mz, double intensity) {
        if (peakList == null) {
            peakList = new HashMap<>(DEFAULT_NUMBER_PEAKS);
        }
        peakList.put(mz, intensity);
    }

    public void setPeakList(Map<Double, Double> peakList) {
        this.peakList = peakList;
    }

    public void setMsLevel(Integer msLevel) {
        this.msLevel = msLevel;
    }

    public void setPrecursorIntensity(Double precursorIntensity) {
        this.precursorIntensity = precursorIntensity;
    }

    public void setPrecursorMz(Double precursorMz) {
        this.precursorMz = precursorMz;
    }

    public void setPrecursorCharge(Integer precursorCharge) {
        this.precursorCharge = precursorCharge;
    }

    public void setUsi(String usi) {
        this.usi = usi;
    }

    public List<String> getProteins() {
        return proteins;
    }

    @Override
    public String toString() {
        return "MspSpectrum{" +
                "index=" + index +
                ", peakList=" + peakList +
                ", msLevel=" + msLevel +
                ", precursorIntensity=" + precursorIntensity +
                ", precursorMz=" + precursorMz +
                ", precursorCharge=" + precursorCharge +
                ", usi='" + usi + '\'' +
                ", attributes=" + properties +
                ", peptideSequence='" + peptideSequence + '\'' +
                ", ptms=" + ptms +
                '}';
    }

    public void setProperties(LibrarySpectrumBuilder builder) {
        this.precursorMz = builder.getPrecursorMz();
        this.ptms = builder.getPtms();
        this.scores = builder.getScores();
        this.proteins = builder.getProteins();

    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public List<Tuple<Integer, String>> getPtms() {
        return ptms;
    }

    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }

    public Map<String, Double> getScores() {
        return this.scores;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
