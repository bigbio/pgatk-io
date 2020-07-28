package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import lombok.Builder;
import lombok.Data;


import java.util.*;
import java.util.stream.Collectors;

@JsonRootName("ArchiveSpectrum")
@JsonTypeName("ArchiveSpectrum")
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveSpectrum implements Spectrum {

    @JsonProperty("usi")
    String usi;

    @JsonProperty("projectAccession")
    String projectAccession;

    @JsonProperty("assayAccession")
    String assayAccession;

    @JsonProperty("spectrumFile")
    String spectrumFile;

    @JsonProperty("sourceID")
    String sourceID;

    @JsonProperty("spectrumTitle")
    String spectrumTitle;

    @JsonProperty("masses")
    Double[] masses;

    @JsonProperty("intensities")
    Double[] intensities;

    @JsonProperty("numPeaks")
    Integer numPeaks;

    @JsonProperty("msLevel")
    Integer msLevel;

    @JsonProperty("precursorCharge")
    Integer precursorCharge;

    @JsonProperty("precursorMz")
    Double precursorMz;

    @JsonProperty("retentionTime")
    Double retentionTime;

    @JsonProperty("properties")
    Set<CvParam> properties;

    /** Interpretation of the Spectra **/

    @JsonProperty("peptideSequence")
    String peptideSequence;

    @JsonProperty("missedCleavages")
    Integer missedCleavages;

    @JsonProperty("modifications")
    Collection<IdentifiedModification> modifications;

    @JsonProperty("annotations")
    List<String> annotations;

    @JsonProperty("isDecoy")
    Boolean isDecoy;

    @JsonProperty("qualityEstimationMethods")
    private Set<CvParam> qualityEstimationMethods;

    @JsonProperty("isValid")
    private Boolean isValid;

    public ArchiveSpectrum() { }

    public ArchiveSpectrum(String usi, String projectAccession, String assayAccession, String spectrumFile, String sourceID, String spectrumTitle, Double[] masses, Double[] intensities, Integer numPeaks, Integer msLevel, Integer precursorCharge, Double precursorMz, Double retentionTime, Set<CvParam> properties, String peptideSequence, Integer missedCleavages, Collection<IdentifiedModification> modifications, List<String> annotations, Boolean isDecoy, Set<CvParam> qualityEstimationMethods, Boolean isValid) {
        this.usi = usi;
        this.projectAccession = projectAccession;
        this.assayAccession = assayAccession;
        this.spectrumFile = spectrumFile;
        this.sourceID = sourceID;
        this.spectrumTitle = spectrumTitle;
        this.masses = masses;
        this.intensities = intensities;
        this.numPeaks = numPeaks;
        this.msLevel = msLevel;
        this.precursorCharge = precursorCharge;
        this.precursorMz = precursorMz;
        this.retentionTime = retentionTime;
        this.properties = properties;
        this.peptideSequence = peptideSequence;
        this.missedCleavages = missedCleavages;
        this.modifications = modifications;
        this.annotations = annotations;
        this.isDecoy = isDecoy;
        this.qualityEstimationMethods = qualityEstimationMethods;
        this.isValid = isValid;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<? extends String> getAdditionalAttributesStrings() {
        return properties.stream().map(CvParam::getName).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<String> getModificationNames() {
        return modifications.stream().map(x -> x.getModificationCvTerm().getName()).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Integer getNumberModifiedSites() {
        return modifications.size();
    }

    public Boolean isDecoy() {
        return isDecoy;
    }


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<? extends CvParam> getAttributes() {
        return properties;
    }

    public Boolean isValid() {
        return isValid;
    }


    @Override
    public Long getIndex() {
        return null;
    }

    @Override
    public String getId() {
        return usi;
    }

    @Override
    public Double getPrecursorMZ() {
        return precursorMz;
    }

    @Override
    public Double getPrecursorIntensity() {
        return null;
    }

    @Override
    public Map<Double, Double> getPeakList() {
        Map<Double, Double> peaks = new HashMap<>();
        for(int i = 0; i < masses.length; i++){
            peaks.put(masses[i], intensities[i]);
        }
        return peaks;
    }

    @Override
    public Collection<? extends Param> getAdditional() {
        List<Param> attributes = new ArrayList<>();
        if(properties != null){
            attributes = properties.stream().map( x-> new io.github.bigbio.pgatk.io.common.CvParam(x.getName(),
                    x.getValue(),x.getCvLabel(),x.getAccession())).collect(Collectors.toList());
        }
        return attributes;
    }
}
