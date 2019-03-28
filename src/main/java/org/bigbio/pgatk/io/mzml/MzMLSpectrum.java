package org.bigbio.pgatk.io.mzml;

import org.bigbio.pgatk.io.common.CvParam;
import org.bigbio.pgatk.io.common.PgatkIOException;
import uk.ac.ebi.jmzml.model.mzml.*;

import java.util.*;

/**
 * A wrapper class around the jmzml Spectrum class implementing the Spectrum interface from the peak-list-parser
 * library.
 *
 * @author jg
 */
public class MzMLSpectrum implements org.bigbio.pgatk.io.common.Spectrum {

    // The spectrum's id in the mzML file
    private final String id;

    // The spectrum index in the file, 1-based.
    private final Long index;

    // The precursor's charge. Null if not available.
    private final Integer charge;

    // The precursor's m/z value. Null if not available.
    private final Double mz;


    // The precursor's intensity. Null if not available.
    private final Double intensity;

    // The spectrum's MS level. Null if not available.

    private final Integer msLevel;

    // The spectrum's peak list as a Map with the m/z values as keys and their intensities as values.
    private final Map<Double, Double> peakList;


    // The spectrum's params
    private final Collection<CvParam> paramGroup;

    /**
     * Creates a new MzMlWrapperSpectrum based
     * on the passed mzML Spectrum object.
     *
     * @param mzMlSpectrum
     * @throws PgatkIOException
     */
    public MzMLSpectrum(uk.ac.ebi.jmzml.model.mzml.Spectrum mzMlSpectrum, Long index) throws PgatkIOException {

        id = mzMlSpectrum.getId();

        this.index = index;

        // get the precursor information. If there are multiple precursors used
        // simply use the first one
        PrecursorList precursorList = mzMlSpectrum.getPrecursorList();

        if (precursorList == null || precursorList.getCount() < 1 || precursorList.getPrecursor().get(0).getSelectedIonList() == null) {
            mz = null;
            intensity = null;
            charge = null;
        } else {
            // check if there are selected ions
            List<CVParam> selectionParams = precursorList.getPrecursor().get(0)
                    .getSelectedIonList().getSelectedIon().get(0).getCvParam();

            CVParam mzParam = getParamFromGroup(selectionParams, MzMlIndexedReader.MZML_PARAMS.SELECTED_MZ.getAccess());
            mz = mzParam != null ? Double.parseDouble(mzParam.getValue()) : null;

            CVParam intensParam = getParamFromGroup(selectionParams, MzMlIndexedReader.MZML_PARAMS.PEAK_INTENSITY.getAccess());
            intensity = intensParam != null ? Double.parseDouble(intensParam.getValue()) : null;

            CVParam chargeParam = getParamFromGroup(selectionParams, MzMlIndexedReader.MZML_PARAMS.CHARGE_STATE.getAccess());
            charge = chargeParam != null ? Integer.parseInt(chargeParam.getValue()) : null;
        }

        CVParam msLevelParam = getParamFromGroup(mzMlSpectrum.getCvParam(), MzMlIndexedReader.MZML_PARAMS.MS_LEVEL.getAccess());
        msLevel = msLevelParam != null ? Integer.parseInt(msLevelParam.getValue()) : null;

        peakList = convertPeakList(mzMlSpectrum.getBinaryDataArrayList());

        paramGroup = createParamGroup(mzMlSpectrum.getCvParam(), mzMlSpectrum.getUserParam());
    }

    /**
     * create a jmzreader param group based on mzml cvParams/userParams
     *
     * @param cvParam
     * @param userParam
     * @return
     */
    private Collection<CvParam> createParamGroup(List<CVParam> cvParam, List<UserParam> userParam) {

        Collection<CvParam> paramGroup = new ArrayList<>();
        if (cvParam != null) {
            for (CVParam cv : cvParam) {
                paramGroup.add(new CvParam(cv.getName(), cv.getValue(), cv.getCvRef(), cv.getAccession()));
            }
        }
        if (userParam != null) {
            for (UserParam up : userParam) {
                paramGroup.add(new CvParam(up.getName(), up.getValue(), null, null));
            }
        }
        return paramGroup;
    }

    /**
     * Converts the spectrum's peak list into
     * a Map as defined by the Spectrum
     * interface.
     *
     * @param binaryDataArrayList
     * @return
     * @throws PgatkIOException
     */
    private Map<Double, Double> convertPeakList(
            BinaryDataArrayList binaryDataArrayList) throws PgatkIOException {
        // make sure the spectrum contains a m/z and an intensity array
        BinaryDataArray mzArray = null, intenArray = null;

        for (BinaryDataArray array : binaryDataArrayList.getBinaryDataArray()) {
            // check the cvParams
            for (CVParam param : array.getCvParam()) {
                if (param.getAccession().equals("MS:1000514")) {
                    mzArray = array;
                    break;
                }
                if (param.getAccession().equals("MS:1000515")) {
                    intenArray = array;
                    break;
                }
            }

            if (mzArray != null && intenArray != null)
                break;
        }

        // if the spectrum doesn't contain a mz and binary array return an empty map
        if (mzArray == null || intenArray == null)
            return Collections.emptyMap();

        // get the values as numbers
        Number mzNumbers[] = mzArray.getBinaryDataAsNumberArray();
        ArrayList<Double> mzValues = new ArrayList<>(mzNumbers.length);

        for (Number n : mzNumbers)
            mzValues.add(n.doubleValue());

        Number intenNumbers[] = intenArray.getBinaryDataAsNumberArray();
        ArrayList<Double> intenValues = new ArrayList<>(intenNumbers.length);

        for (Number n : intenNumbers)
            intenValues.add(n.doubleValue());

        // make sure both have the same size
        if (intenValues.size() != mzValues.size())
            throw new PgatkIOException("Different sizes for m/z and intensity value arrays for spectrum " + id);

        // create the map
        Map<Double, Double> peakList = new HashMap<>(mzNumbers.length);

        for (int i = 0; i < mzNumbers.length; i++)
            peakList.put(mzValues.get(i), intenValues.get(i));

        return peakList;
    }

    @Override
    public Long getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public Integer getPrecursorCharge() {
        return charge;
    }

    public Double getPrecursorMZ() {
        return mz;
    }

    public Double getPrecursorIntensity() {
        return intensity;
    }

    public Map<Double, Double> getPeakList() {
        return peakList;
    }

    public Integer getMsLevel() {
        return msLevel;
    }

    /**
     * Returns the (first) parameter with the given
     * accession from the passed parameter group or
     * null in case no parameter with the given
     * accession exists.
     *
     * @param params
     * @param accession
     * @return The first CVParam identified by this accession or null in case no parameter with that accession exists.
     */
    private CVParam getParamFromGroup(List<CVParam> params, String accession) {
        for (CVParam p : params) {
            if (p.getAccession().equals(accession))
                return p;
        }

        return null;
    }

    @Override
    public Collection<CvParam> getAdditional() {
        return paramGroup;
    }
}