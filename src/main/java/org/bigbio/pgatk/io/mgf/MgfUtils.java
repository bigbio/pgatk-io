package org.bigbio.pgatk.io.mgf;

import java.util.regex.Pattern;

public class MgfUtils {

    public static int BUFFER_SIZE = 1024 * 100;

    public enum FragmentToleranceUnits {DA, MMU}

    public enum MassType {MONOISOTOPIC, AVERAGE}

    public enum SearchType {
        PMF("PMF"), SQ("SQ"), MIS("MIS");
        private String name;

        SearchType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ReportType {
        PROTEIN("protein"), PEPTIDE("peptide"), ARCHIVE("archive"),
        CONCISE("concise"), SELECT("select"), UNASSIGNED("unassigned");

        private String name;

        ReportType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum PeptideToleranceUnit {
        PERCENT("%"), PPM("ppm"), MMU("mmu"), DA("Da");

        private String name;

        PeptideToleranceUnit(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final boolean  DEFAULT_ALLOW_CUSTOM_TAGS = false;
    public static final boolean DEFAULT_IGNORE_WRONG_PEAKS = false;

    /**
     * Regex to capture mgf comments in mgf files.
     */
    public static final String mgfCommentRegex = "^[#;!/].*";
    /**
     * Regex to recognize a attribute and extract its name and value
     */
    public static final Pattern attributePattern = Pattern.compile("(\\w+)\\[?\\d?\\]?\\s*=(.*)\\s*");

}
