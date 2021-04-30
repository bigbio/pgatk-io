package io.github.bigbio.pgatk.io.pride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;
import io.github.bigbio.pgatk.utilities.spectra.SpectraUtilities;
import lombok.Data;
import org.apache.avro.reflect.Nullable;

import java.util.*;

@JsonRootName("AnnotatedSpectrum")
@JsonTypeName("AnnotatedSpectrum")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * The {@link AnnotatedSpectrum} is a key value compatible with parquet and other column store systems to store PeptideSpectrum
 * Matches. The Collections properties of the Spectrum are annotated as Lists.
 *
 * @author ypriverol
 */
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
     * Protein accessions. A {@link List} of protein accessions from Uniprot, ENSEMBL, or other major databases.
     */
    @JsonProperty("proteinAccessions")
    private List<String> proteinAccessions;

    /**
     * Gene related accessions, including: Transcript, Exon Accessions from multiple databases.
     */
    @JsonProperty("geneAccessions")
    private List<String> geneAccessions;

    /**
     * Protein localization including:
     *  - Accession of the protein
     *  - start: start in the sequence 1-based
     *  - end: end in the sequence (start + length (peptideSequence))
     */
    @JsonProperty("proteinLocalizations")
    private List<AccessionLocalization> proteinLocalizations;

    /**
     * Gene Coordinates are more complex than Protein localizations, it contains, Transcript
     * position, Gene positions and exon information.
     */
    @JsonProperty("geneLocalizations")
    private List<GeneCoordinates> geneLocalizations;


    @JsonProperty("sampleAccession")
    @Nullable
    String sampleAccession;

    /**
     * The organism where the peptide has been found/identified. In PRIDE some peptides are associated to more than one
     * species, a unique species/organism should be selected.
     */
    @JsonProperty("organism")
    @Nullable
    private String organism;

    /**
     * Sample information from SDRF, the sample information a a list of key value pairs from samples,
     * for example:
     *  - organism:homo sapiens
     *  - organism part: brain
     */
    @JsonProperty("sample")
    private List<AvroTuple> sample;

    /**
     * Additional biological annotations for the peptide as keywords, for example:
     *  - non-canonical
     *  - unique peptide
     *  - variant
     */
    @JsonProperty("biologicalAnnotations")
    List<AvroTuple> biologicalAnnotations;

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
    private List<AvroModification> modifications;


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
    private List<AvroTerm> qualityScores;

    /**
     * A list of String values that to characterize the MS information, example:
     * - pass FDR threshold
     * - Orbitrap LTQ
     * - Label-free
     */
    @JsonProperty("msAnnotations")
    private List<AvroTuple> msAnnotations;

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


    public AnnotatedSpectrum() {
    }

    public AnnotatedSpectrum(String usi, String pepSequence, String peptidoform, List<String> proteinAccessions, List<String> geneAccessions, List<AccessionLocalization> proteinLocalizations,
                             List<GeneCoordinates> geneLocalizations, String sampleAccession, String organism, List<AvroTuple> sample, List<AvroTuple> biologicalAnnotations,
                             double precursorMz, Integer precursorCharge, List<AvroModification> modifications, List<Double> masses,
                             List<Double> intensities, Double retentionTime, Integer msLevel, Integer missedCleavages, List<AvroTerm> qualityScores,
                             List<AvroTuple> msAnnotations, String pxAccession, Boolean isDecoy, Double peptideIntensity) {
        this.usi = usi;
        this.pepSequence = pepSequence;
        this.peptidoform = peptidoform;
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
        this.proteinAccessions = (proteinAccessions != null)?new ArrayList<>(new HashSet<>(proteinAccessions)):null;
        this.geneAccessions = (geneAccessions != null)?new ArrayList<>(new HashSet<>(geneAccessions)):null;
        this.binaryPeaks = new BinaryPeaks(masses, intensities);
        this.proteinLocalizations = proteinLocalizations;
        this.geneLocalizations = geneLocalizations;
    }

    @Override
    /**
     * This function is needed in the {@link Spectrum} interface but
     * is not implemented in this class.
     */
    public Long getIndex() {
        return null;
    }

    @Override
    public String getId() {
        return this.usi;
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
    /**
     * This method is mandatory for {@link Spectrum} but is not implemented by {@link AnnotatedSpectrum}
     */
    public Double getPrecursorIntensity() {
        return null;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    /**
     * Peaks are stored as bytes in Parquet using the {@link BinaryPeaks}. These peaks are converted to a Map using this
     * function.
     */
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
    public Integer getMsLevel() {
        return msLevel;
    }

    @Override
    public Collection<? extends Param> getAdditional() {
        List<Param> attributes = new ArrayList<>();
        return attributes;
    }

    @Override
    public String toString() {
        return "AnnotatedSpectrum{" +
                "usi='" + usi + '\'' +
                ", pepSequence='" + pepSequence + '\'' +
                ", peptidoform='" + peptidoform + '\'' +
                ", proteinAccessions=" + proteinAccessions +
                ", geneAccessions=" + geneAccessions +
                ", proteinLocalizations=" + proteinLocalizations +
                ", geneLocalizations=" + geneLocalizations +
                ", sampleAccession='" + sampleAccession + '\'' +
                ", organism='" + organism + '\'' +
                ", sample=" + sample +
                ", biologicalAnnotations=" + biologicalAnnotations +
                ", precursorMz=" + precursorMz +
                ", precursorCharge=" + precursorCharge +
                ", modifications=" + modifications +
                ", binaryPeaks=" + binaryPeaks +
                ", retentionTime=" + retentionTime +
                ", msLevel=" + msLevel +
                ", missedCleavages=" + missedCleavages +
                ", qualityScores=" + qualityScores +
                ", msAnnotations=" + msAnnotations +
                ", pxAccession='" + pxAccession + '\'' +
                ", isDecoy=" + isDecoy +
                ", peptideIntensity=" + peptideIntensity +
                '}';
    }

    public List<Double> getMasses() {
        List<Double> masses = new ArrayList<>();
        if(binaryPeaks != null)
            masses = SpectraUtilities.decodeBinary(binaryPeaks.getBinaryMasses());
        return masses;
    }


    public List<Double> getIntensities() {
        List<Double> intensities = new ArrayList<>();
        if(binaryPeaks != null)
            intensities = SpectraUtilities.decodeBinary(binaryPeaks.getBinaryIntensities());
        return intensities;
    }
}
