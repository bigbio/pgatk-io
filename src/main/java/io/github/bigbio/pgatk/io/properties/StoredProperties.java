package io.github.bigbio.pgatk.io.properties;

/**
 * Defined the stored properties.
 *
 * @author jg
 * @author ypriverol
 */
public class StoredProperties {
    private StoredProperties() {}

    public static final String ORG_FILENAME = "Filename";
    public static final String FILE_INDEX = "Index";
    public static final String FILE_SCAN  = "Scan";
    public static final String TITLE = "spectrum title";
    public static final String PRECURSOR_MZ = "Prec_mz";
    public static final String SEQUENCE = "Sequence";
    public static final String PTMS = "PTMs";
    public static final String CHARGE = "Charge";
    public static final String RETENTION_TIME = "RT";
    public static final String ORIGINAL_PEAKS_MZ = "org_peaks_mz";
    public static final String ORIGINAL_PEAKS_INTENS = "org_peaks_intens";
    /**
     * A "," delimited string of search engine scores
     */
    public static final String SEARCH_ENGINE_SCORES = "search_scores";
    /**
     * A "," delimited string of search engine score types / names
     */
    public static final String SEARCH_ENGINE_SCORE_TYPES = "search_score_types";
}
