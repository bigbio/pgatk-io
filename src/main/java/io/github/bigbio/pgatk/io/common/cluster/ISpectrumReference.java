package io.github.bigbio.pgatk.io.common.cluster;

import io.github.bigbio.pgatk.io.common.psms.IPeptideSpectrumMatch;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.util.List;

/**
 * Created by jg on 01.08.14.
 */
public interface ISpectrumReference extends Spectrum {

    float getSimilarityScore();

    String getSpecies();

    boolean isIdentifiedAsMultiplePeptides();

    boolean isIdentified();

    IPeptideSpectrumMatch getMostCommonPSM();

    List<IPeptideSpectrumMatch> getPSMs();

    boolean hasPeaks();

    List<ClusteringFileSpectrumReference.Peak> getPeaks();

    void addPeaksFromString(String mzString, String intensityString) throws Exception;

    /**
     * Spectrum source filenames may be reported using a special id format. In cases
     * were this format was used, this function returns the spectrum's source filename.
     * @return The spectrum's source filename or NULL in case it wasn't reported.
     */
    String getSourceFilename();

    /**
     * Spectrum source filename and original id within may be reported using a special id format. In cases
     * were this format was used, this function returns the spectrum's id (should be according to the PSI
     * convention for referencing spectra in peak list formats such as done in mzTab).
     * @return The spectrum's source id or NULL in case it wasn't reported.
     */
    String getSourceId();

    /**
     * If the spectrum source file and source index were reported using the special format, this function
     * returns the original spectrum's title without the added information.
     * Otherwise, the whole title is returned.
     * @return The spectrum's original title
     */
    String getOriginalSpectrumTitle();
}
