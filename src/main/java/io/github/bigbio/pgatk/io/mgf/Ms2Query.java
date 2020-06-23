package io.github.bigbio.pgatk.io.mgf;

import io.github.bigbio.pgatk.io.common.CvParam;
import io.github.bigbio.pgatk.io.common.Param;
import io.github.bigbio.pgatk.io.common.PgatkIOException;
import io.github.bigbio.pgatk.io.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.util.*;
import java.util.regex.Matcher;

/**
 * This class reads the MS2 information in the MGF.
 *
 * @author ypriverol
 *
 */

@Slf4j
public class Ms2Query implements Spectrum {

  private static final int DEFAULT_NUMBER_PEAKS = 100;

  // It is important to notice that
  private final int DEFAULT_MS_LEVEL = 2;

  // Optional title of the spectrum identification
  private String title;

  // Optional charge state of the precursor peptide.
  private String chargeState;

  // Optional peptide tolerance
  private Double tolerance;

  // Optional peptide tolerance unit
  private MgfUtils.PeptideToleranceUnit toleranceUnit;

  // Optional sequence qualifiers
  // See the Mascot documentation for detailed information (http://www.matrixscience.com/help/sq_help.html#SEQ)
  private List<String> sequenceQualifiers;

  // Optional amino acid composition
  private String composition;

  // Sequence tags
  // See the Mascot documentation for detailed information (http://www.matrixscience.com/help/sq_help.html#TAG)
  private List<String> tags;

  // Error tolerant sequence tags
  private List<String> errorTolerantTags;

  // Optional scan number or range
  private String scan;

  // RAW Original scan Number
  private String rawScan;

  // Optional retention time or range in seconds
  private String retentionTime;

  // Optional MS/MS ion series to be used based on the instrument as defined in fragmentation_rules
  private String instrument;

  // Optional variable modifications as defined in unimod.xml
  private String variableModifications;

  // The spectrum's peaks
  private Map<Double, Double> peakList;

  // Optional peptide mass
  private Double peptideMass;

  // Optional peptide intensity
  private Double peptideIntensity;

  // 1-based index of the spectrum in the file.
  private Long index;

  //MGF allows user tags in the format USER[NN] like USER01, USER02
  private Map<Integer, String> userTags = new HashMap<>();

  private final boolean disableCommentSupport;

  // Ms level
  private Integer msLevel = DEFAULT_MS_LEVEL;

  /**
   * Default constructor generating an empty Ms2Query
   */
  public Ms2Query(boolean disableCommentSupport) {
    this.disableCommentSupport = disableCommentSupport;
  }

  /**
   * Generates a Ms2Query from a mgf part representing an Ms2Query (including "BEGIN IONS" and "END IONS")
   * @param mgfQuery the mgf part to parse
   * @param index RAW Original scan Number
   * @param disableCommentSupport true to disable comment support, false otherwise
   * @throws PgatkIOException any problems parsing the mgf part
   */
  public Ms2Query(String mgfQuery, int index, boolean disableCommentSupport, boolean ignoreWrongPeaks) throws PgatkIOException {

    this.disableCommentSupport = disableCommentSupport;
    this.index = (long) index;

    String[] lines = mgfQuery.trim().split("\n");

    boolean inAttributeSection = true;

    for (int nLineNumber = 0; nLineNumber < lines.length; nLineNumber++) {
      String line = lines[nLineNumber].trim();
      // remove comments from the line
      //TODO Check if the comment are inside BEGIN/ENDS IONS because comments are not allowed between  BEGIN/ENDS IONS by the specification
      if (!disableCommentSupport)
        line = line.replaceAll(MgfUtils.mgfCommentRegex, line);
      if (line.length() < 1) { // ignore empty lines
        continue;
      }
      // first line must be "BEGIN IONS" and last line must be "END IONS"
      if (nLineNumber == 0 && !"BEGIN IONS".equals(StringUtils.removeBOMString(line))) {
        throw new PgatkIOException("MS2 query must start with 'BEGIN IONS'");
      }
      if (nLineNumber == 0) {
        continue;
      }
      if (nLineNumber == lines.length - 1 && !"END IONS".equals(line)) {
        throw new PgatkIOException("MS2 query must end with 'END IONS'");
      }
      if (nLineNumber == lines.length - 1) {
        continue;
      }
      Matcher attributeMatcher = MgfUtils.attributePattern.matcher(line); // check if it's a property
      boolean matchesAttributePattern = false;
      if (inAttributeSection) {
        matchesAttributePattern = attributeMatcher.find();
      }
      if (matchesAttributePattern) {
        if (attributeMatcher.groupCount() != 2) {
          throw new PgatkIOException("Invalid attribute line encountered in MS2 query: " + line);
        }
        String name = attributeMatcher.group(1);
        String value = attributeMatcher.group(2);
        saveAttribute(name, value);
      } else {
        double[] peakArray = MgfUtils.parsePeakLine(line);
        if (peakArray != null && peakArray.length == 2) {
          addPeak(peakArray[0], peakArray[1]);
        } else {  // no index could be found
          if (ignoreWrongPeaks) {
            log.error("The following peaks and wronly annotated -- " + line);
          } else
            throw new NoSuchElementException("Unable to parse 'mz' and 'intensity' values for " + line);
        }
        inAttributeSection = false;
      }
    }
  }

  /**
   * Stores the attribute in the respective member variable.
   * @param name  The attribute's name
   * @param value The attribute's value
   * @throws PgatkIOException any problems saving the attribute
   */
  public void saveAttribute(String name, String value) {
    if ("TITLE".equals(name)) {
      title = value;
    } else if ("CHARGE".equals(name)) {
      chargeState = value;
    } else if ("TOL".equals(name)) {
      tolerance = Double.parseDouble(value);
    } else if ("TOLU".equals(value)) {
      if ("%".equals(value)) {
        toleranceUnit = MgfUtils.PeptideToleranceUnit.PERCENT;
      }
      if ("ppm".equals(value)) {
        toleranceUnit = MgfUtils.PeptideToleranceUnit.PPM;
      }
      if ("mmu".equals(value)) {
        toleranceUnit = MgfUtils.PeptideToleranceUnit.MMU;
      }
      if ("Da".equals(value)) {
        toleranceUnit = MgfUtils.PeptideToleranceUnit.DA;
      }
      if (toleranceUnit == null) {
        throw new IllegalStateException("Invalid tolerance unit set.");
      }
    } else if ("SEQ".equals(name)) {
      addSequenceQualifier(value);
    } else if ("COMP".equals(name)) {
      composition = value;
    } else if ("TAG".equals(name)) {
      addTag(value);
    } else if ("ETAG".equals(name)) {
      addETag(value);
    } else if ("SCANS".equals(name)) {
      scan = value;
    } else if ("RAWSCANS".equals(name)) {
      rawScan = value;
    } else if ("RTINSECONDS".equals(name)) {
      retentionTime = value;
    } else if ("INSTRUMENT".equals(name)) {
      instrument = value;
    } else if ("IT_MODS".equals(name)) {
      // TODO: make sure IT_MODS are handeled correctly
      variableModifications = value;
    } else if ("PEPMASS".equals(name)) {
      if (value.trim().split("\\s+").length > 1) { // support optional intensity values
        String[] parts = value.trim().split("\\s+");
        peptideMass = Double.parseDouble(parts[0]);
        peptideIntensity = Double.parseDouble(parts[1]);
      } else {
        peptideMass = Double.parseDouble(value);
      }
    } else if (name != null && name.startsWith("USER")) {
      Integer index = Integer.parseInt(name.substring("USER".length()));
      userTags.put(index, value);
    } // if the tag is not known, simply ignore it
  }

  /**
   * Adds a peak to the spectrum.
   * @param mz the mz value
   * @param intensity the intensity value
   */
  public void addPeak(Double mz, Double intensity) {
    if (peakList == null) {
      peakList = new HashMap<>(DEFAULT_NUMBER_PEAKS);
    }
    peakList.put(mz, intensity);
  }

  /**
   * Ads a sequence qualifier.
   * @param sequenceQualifier the sequence qualifier to put.
   */
  public void addSequenceQualifier(String sequenceQualifier) {
    if (sequenceQualifiers == null) {
      sequenceQualifiers = new ArrayList<>();
    }
    sequenceQualifiers.add(sequenceQualifier);
  }

  /**
   * Adds a tag.
   * @param tag the tag to put.
   */
  public void addTag(String tag) {
    if (tags == null) {
      tags = new ArrayList<>();
    }
    tags.add(tag);
  }

  /**
   * Adds an error tolerant tag.
   * @param errorTolerantTag the error tolerant tag
   */
  public void addETag(String errorTolerantTag) {
    addErrorTag(errorTolerantTag);
  }

  /**
   * Adds an error tolerant tag.
   * @param errorTolerantTag the error tolerant tag
   */
  public void addErrorTag(String errorTolerantTag) {
    if (errorTolerantTags == null) {
      errorTolerantTags = new ArrayList<>();
    }
    errorTolerantTags.add(errorTolerantTag);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getChargeState() {
    return chargeState;
  }

  public void setChargeState(String chargeState) {
    this.chargeState = chargeState;
  }

  public Double getTolerance() {
    return tolerance;
  }

  public void setTolerance(Double tolerance) {
    this.tolerance = tolerance;
  }

  public MgfUtils.PeptideToleranceUnit getToleranceUnit() {
    return toleranceUnit;
  }

  public void setToleranceUnit(MgfUtils.PeptideToleranceUnit toleranceUnit) {
    this.toleranceUnit = toleranceUnit;
  }

  public String getComposition() {
    return composition;
  }

  public void setComposition(String composition) {
    this.composition = composition;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getRetentionTime() {
    return retentionTime;
  }

  public void setRetentionTime(String retentionTime) {
    this.retentionTime = retentionTime;
  }

  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  public List<String> getSequenceQualifiers() {
    return sequenceQualifiers;
  }

  public void setSequenceQualifiers(List<String> sequenceQualifiers) {
    this.sequenceQualifiers = sequenceQualifiers;
  }

  public List<String> getErrorTolerantTags() {
    return errorTolerantTags;
  }

  public void setErrorTolerantTags(List<String> errorTolerantTags) {
    this.errorTolerantTags = errorTolerantTags;
  }

  public String getScan() {
    return scan;
  }

  public void setScan(String scan) {
    this.scan = scan;
  }

  public String getVariableModifications() {
    return variableModifications;
  }

  public void setVariableModifications(String variableModifications) {
    this.variableModifications = variableModifications;
  }

  public Map<Double, Double> getPeakList() {
    return peakList;
  }

  public void setPeakList(Map<Double, Double> peakList) {
    this.peakList = peakList;
  }

  public Double getPeptideMass() {
    return peptideMass;
  }

  public void setPeptideMass(Double peptideMass) {
    this.peptideMass = peptideMass;
  }

  public Double getPeptideIntensity() {
    return peptideIntensity;
  }

  public void setPeptideIntensity(Double peptideIntensity) {
    this.peptideIntensity = peptideIntensity;
  }

  public Map<Integer, String> getUserTags() {
    return userTags;
  }

  public void setUserTags(Map<Integer, String> userTags) {
    this.userTags = userTags;
  }

  /**
   * Compiles all the information from this Ms2Query object.
   * @return a String of all the information from this object.
   */
  @Override
  public String toString() {
    StringBuilder query = new StringBuilder("BEGIN IONS\n");
    // process the optional attributes
    if (chargeState != null) {
      query.append("CHARGE=").append(chargeState).append('\n');
    }
    if (composition != null) {
      query.append("COMP=").append(composition).append('\n');
    }
    if (errorTolerantTags != null && errorTolerantTags.size() > 0) {
      for (String tag : errorTolerantTags) {
        query.append("ETAG=").append(tag).append('\n');
      }
    }
    if (instrument != null) {
      query.append("INSTRUMENT=").append(instrument).append('\n');
    }
    if (variableModifications != null) {
      query.append("IT_MODS=").append(variableModifications).append('\n');
    }
    if (peptideMass != null) {
      query.append("PEPMASS=").append(peptideMass).append('\n');
    }
    if (retentionTime != null) {
      query.append("RTINSECONDS=").append(retentionTime).append('\n');
    }
    if (scan != null) {
      query.append("SCANS=").append(scan).append('\n');
    }
    if (sequenceQualifiers != null) {
      for (String qual : sequenceQualifiers) {
        query.append("SEQ=").append(qual).append('\n');
      }
    }
    if (tags != null) {
      for (String tag : tags) {
        query.append("TAG=").append(tag).append('\n');
      }
    }
    if (title != null) {
      query.append("TITLE=").append(title).append('\n');
    }
    if (tolerance != null) {
      query.append("TOL=").append(tolerance).append('\n');
    }
    if (toleranceUnit != null) {
      query.append("TOLU=").append(toleranceUnit).append('\n');
    }
    List<Double> masses = new ArrayList<>(peakList.keySet());
    Collections.sort(masses);
    for (Double mz : masses) {
      query.append(mz).append(' ').append(peakList.get(mz)).append('\n');
    }
    query.append("END IONS\n");
    return query.toString();
  }

  @Override
  public Long getIndex() {
    return index;
  }

  /**
   * Gets the ID.
   * @return the ID. If none is set, null is returned,
   */
  public String getId() {
    String result = null;
    if (index != null) {
      result = index.toString();
    }
    return result;
  }

  public void setIndex(long index){
    this.index = index;
  }


  /**
   * Gets the precursor charge.
   * @return the precursor charge. If there is no charge state, or if there are multiple charge states
   * then null is returned.
   */
  public Integer getPrecursorCharge() {
    Integer result = null;
    if (chargeState!=null && !chargeState.isEmpty()) {
      if (!chargeState.contains(",") && !chargeState.contains("and")) {
        String modifiedChargeState = chargeState;
        if (modifiedChargeState.contains(".")) { // value should be reported as integer not decimal
          modifiedChargeState = modifiedChargeState.substring(0, modifiedChargeState.indexOf("."));
        }
        if (modifiedChargeState.contains("+")) { // positive "+x"
          modifiedChargeState = modifiedChargeState.replace("+", "");
        } // else may be negative "-x"
        if (StringUtils.isInteger(modifiedChargeState)) {
          result = Integer.parseInt(modifiedChargeState);
        } else {
          throw new NumberFormatException("Unable to parse chargeState: " + chargeState);
        }
      } // else there are multiple charge states, give up (null)
    } // else no charge state found (null)
    return result;
  }

  public Double getPrecursorMZ() {
    return peptideMass;
  }

  public Double getPrecursorIntensity() {
    return peptideIntensity;
  }

  @Override
  public Integer getMsLevel() {
    return msLevel; // can only be a MS2 level spectrum
  }

  /**
   * Get Attributes as {@link CvParam}
   *
   * @return the additional paramgroup.
   */
  @Override
  public Collection<? extends Param> getAdditional() {

    List<CvParam> paramGroup = new ArrayList<>();

    if (retentionTime != null) {
      paramGroup.add(new CvParam("retention time", retentionTime, "MS", "MS:1000894"));
    }
    if (scan != null) {
      paramGroup.add(new CvParam("peak list scans", scan, "MS", "MS:1000797"));
    }
    if (title != null) {
      paramGroup.add(new CvParam("spectrum title", title, "MS", "MS:1000796"));
    }
    if (tolerance != null) {
      paramGroup.add(new CvParam("Fragment mass tolerance setting", tolerance.toString(), "PRIDE", "PRIDE:0000161"));
    }
    if (toleranceUnit != null) {
      paramGroup.add(new CvParam("Fragment mass tolerance unit", toleranceUnit.toString(), null, null));
    }
    if (instrument != null) {
      paramGroup.add(new CvParam("Instrument", instrument, null, null));
    }
    if (sequenceQualifiers != null && sequenceQualifiers.size() == 1) {
      paramGroup.add(new CvParam("Sequence", sequenceQualifiers.get(0), null, null));
    }
    for (Map.Entry<Integer, String> integerStringEntry : userTags.entrySet()) {
      String userIndexString = String.valueOf(integerStringEntry.getKey());
      if (integerStringEntry.getKey() < 10) {
        userIndexString = '0' + userIndexString;
      }
      paramGroup.add(new CvParam("USER" + userIndexString, integerStringEntry.getValue(), null, null));
    }
    return paramGroup;
  }
}