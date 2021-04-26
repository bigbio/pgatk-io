package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.io.utils.Tuple;
import io.github.bigbio.pgatk.utilities.spectra.SpectraUtilities;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@JsonRootName("AnnotatedSpectrum")
@JsonTypeName("AnnotatedSpectrum")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotatedSpectrum implements Spectrum {

    /**
     * USI of the spectra. The USI is the accession of the project that originate the dataset, the collection (RAW file)
     * that contains the spectrum and the scan number of the spectra.
     */
    @JsonProperty("usi")
    private String usi;

    /**
     * The peptide sequence.
     */
    @JsonProperty("peptideSequence")
    private String pepSequence;

    /**
     * The peptidoform information. The combination of peptide Sequence + modification in position.
     */
    @JsonProperty("peptidoform")
    private String peptidoform;

    // Sample information

    /**
     * Protein accessions. A {@link Set} of protein accessions from Uniprot, ENSEMBL, or other major databases.
     */
    @JsonProperty("proteinAccessions")
    private Set<String> proteinAccessions;

    /**
     * Gene related accessions, including: Transcript, Exon Accessions from multiple databases.
     */
    @JsonProperty("geneAccessions")
    private Set<String> geneAccessions;

    /**
     * Protein localization including:
     *  - Accession of the protein
     *  - start: start in the sequence 1-based
     *  - end: end in the sequence (start + length (peptideSequence))
     */
    @JsonProperty("proteinLocalizations")
    private Set<AccessionLocalization> proteinLocalizations;

    /**
     * Gene Coordinates are more complex than Protein localizations, it contains, Transcript position, Gene
     * positions and exon information.
     */
    @JsonProperty("geneLocalizations")
    private Set<GeneCoordinates> geneLocalizations;


    @JsonProperty("sampleAccession")
    String sampleAccession;

    /**
     * The organism where the peptide has been found/identified. In PRIDE some peptides are associated to more than one
     * species, a unique species/organism should be selected.
     */
    @JsonProperty("organism")
    private String organism;

    /**
     * Sample information from SDRF, the sample information a a list of key value pairs from samples, for example:
     *  - organism:homo sapiens
     *  - organism part: brain
     */
    @JsonProperty("sample")
    private List<Tuple<String, String>> sample;

    /**
     * Additional biological annotations for the peptide as keywords, for example:
     *  - non-canonical
     *  - unique peptide
     *  - variant
     */
    @JsonProperty("biologicalAnnotations")
    List<Tuple<String, String>> biologicalAnnotations;

    // Information about the Mass spectrometry (Spectrum)

    /**
     * Precursor Mz
     */
    @JsonProperty("precursorMz")
    private double precursorMz;

    /**
     * Precursor charge
     */
    @JsonProperty("precursorCharge")
    private Integer precursorCharge;

    /**
     * Structure of Post-translational modifications as position+name-modification+score of the quality of PTM.
     */
    @JsonProperty("modifications")
    private List<IdentifiedModification> modifications;


    @JsonProperty("binaryPeaks")
    private BinaryPeaks binaryPeaks;

    /**
     * Spectrum retention time.
     */
    @JsonProperty("retentionTime")
    Double retentionTime;

    /**
     * Spectrum retention time.
     */
    @JsonProperty("msLevel")
    Integer msLevel;

    /**
     * Number of missclevages for the peptide, this is related with the cleavage agent used to generate the peptide
     */
    @JsonProperty("missedCleavages")
    Integer missedCleavages;

    /**
     * List of spectrum identification scores and statistical assessment scores such as q-value, posterior error
     * probabilities, or p-values.
     *
     */
    @JsonProperty("qualityScores")
    private Set<CvParam> qualityScores;

    /**
     * A list of String values that to characterize the MS information, example:
     * - pass FDR threshold
     * - Orbitrap LTQ
     * - Label-free
     */
    @JsonProperty("msAnnotations")
    private List<Tuple<String, String>> msAnnotations;

    /**
     * A list of ProteomeXchange projects that has been used to generate the following peptide.
     */
    @JsonProperty("pxAccession")
    private String pxAccession;

    /**
     * A list of ProteomeXchange projects that has been used to generate the following peptide.
     */
    @JsonProperty("isDecoy")
    private Boolean isDecoy;

    /**
     * Raw peptide intensity in the sample
     */
    @JsonProperty("peptideIntensity")
    private Double peptideIntensity;

    /**
     * Default constructor
     */
    public AnnotatedSpectrum() {
    }

    public AnnotatedSpectrum(String usi, String pepSequence, String peptidoform, Set<String> proteinAccessions,
                             Set<String> geneAccessions, Set<AccessionLocalization> proteinLocalizations,
                             Set<GeneCoordinates> geneLocalizations, String sampleAccession, String organism,
                             List<Tuple<String, String>> sample, List<Tuple<String, String>> biologicalAnnotations,
                             double precursorMz, Integer precursorCharge, List<IdentifiedModification> modifications,
                             List<Double> masses, List<Double> intensities, Double retentionTime, Integer msLevel,
                             Integer missedCleavages, Set<CvParam> qualityScores, List<Tuple<String, String>>
                             msAnnotations, String pxAccession, Boolean isDecoy, Double peptideIntensity) {
        this.usi = usi;
        this.pepSequence = pepSequence;
        this.peptidoform = peptidoform;
        this.proteinAccessions = proteinAccessions;
        this.geneAccessions = geneAccessions;
        this.proteinLocalizations = proteinLocalizations;
        this.geneLocalizations = geneLocalizations;
        this.sampleAccession = sampleAccession;
        this.organism = organism;
        this.sample = sample;
        this.biologicalAnnotations = biologicalAnnotations;
        this.precursorMz = precursorMz;
        this.precursorCharge = precursorCharge;
        this.modifications = modifications;
        this.retentionTime = retentionTime;
        this.msLevel = msLevel;
        this.missedCleavages = missedCleavages;
        this.qualityScores = qualityScores;
        this.msAnnotations = msAnnotations;
        this.pxAccession = pxAccession;
        this.isDecoy = isDecoy;
        this.peptideIntensity = peptideIntensity;
        this.binaryPeaks = new BinaryPeaks(masses, intensities);
    }

    @Override
    public Long getIndex() {
        return null;
    }

    @Override
    public String getId() {
        return this.usi;
    }

    @Override
    public Double getPrecursorMZ() {
        return this.precursorMz;
    }

    @Override
    public Double getPrecursorIntensity() {
        return null;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Map<Double, Double> getPeakList() {
        Map<Double, Double> peaks = new HashMap<>();
        if(binaryPeaks != null){
            List<Double> masses = SpectraUtilities.decodeBinary(binaryPeaks.getBinaryMasses());
            List<Double> intensities = SpectraUtilities.decodeBinary(binaryPeaks.getBinaryIntensities());
            for(int i = 0; i < masses.size(); i++){
                peaks.put(masses.get(i), intensities.get(i));
            }
        }
        return peaks;
    }

    @Override
    public Collection<? extends Param> getAdditional() {
        List<Param> attributes = new ArrayList<>();
        if(qualityScores != null){
            attributes = qualityScores.stream()
                    .map( x-> new io.github.bigbio.pgatk.io.common.CvParam(x.getName(),
                            x.getValue(),x.getCvLabel(),x.getAccession())).collect(Collectors.toList());
        }
        return attributes;
    }

    @Override
    public String toString() {
        return "AnnotatedSpectrum{" +
                "usi='" + usi + '\'' +
                ", pepSequence='" + pepSequence + '\'' +
                ", peptidoform='" + peptidoform + '\'' +
                ", organism='" + organism + '\'' +
                ", sample=" + sample +
                '}';
    }
}
