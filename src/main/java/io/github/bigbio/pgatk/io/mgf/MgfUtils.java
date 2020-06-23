package io.github.bigbio.pgatk.io.mgf;

import java.util.regex.Pattern;

public class MgfUtils {

    public static int BUFFER_SIZE = 1024 * 100;

    public static double[] parsePeakLine(String line) {
        char[] lineArray = line.toCharArray();
        //If available
        boolean secondGap = false;
        boolean massFound = false;
        boolean intensityFound = false;
        StringBuilder mass = new StringBuilder(100);
        StringBuilder intensity = new StringBuilder(100);
        for( int i = 0; i < line.length(); i++){
            if(lineArray[i] == ' ' || lineArray[i] == '\t'){
               if(massFound)
                   secondGap = true;
               if(intensityFound)
                   break;
            }else {
                if(!secondGap){
                    mass.append(lineArray[i]);
                    massFound = true;
                }else{
                    intensity.append(lineArray[i]);
                    intensityFound = true;

                }
            }
        }
        if(intensity.length() > 0 && mass.length() > 0){
            double[] peaks = new double[2];
            peaks[0] = Double.parseDouble(mass.toString());
            peaks[1] = Double.parseDouble(intensity.toString());
            return peaks;
        }

        return null;

    }

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
