package org.bigbio.pgatk.io.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a spectrum in the
 * parsed peak list file.
 *
 * @author jg
 * @author ypriverol
 *
 */
public interface Spectrum extends Serializable {

	/**
	 * Internal Spectrum in the corresponding file MGF or mzXML or mzML, starting from 1, incrementing by one for each consecutive spectrum. This is
	 * the number is used in the mzIdentML and mzTab files to reference the Spectrum.
	 */
	Long getIndex();

	/**
	 * The textual representation of an ID of a scan. mzML provides those IDs by default, in mzXML
	 * there is no such element, so this will return the same as {@link #getIndex()}}.
	 */
    String getId();
	
	/**
	 * Returns the spectrum's charge or
	 * null in case the charge is not
	 * available.
	 * @return
	 */
	Integer getPrecursorCharge();
	
	/**
	 * Returns the precursor's m/z or
	 * null in case the precursor's m/z
	 * is not available.
	 * @return
	 */
	Double getPrecursorMZ();
	
	/**
	 * Returns the precursor's intensity
	 * or null in case it it not available.
	 * @return
	 */
	Double getPrecursorIntensity();

	/**
	 * Returns the spectrum's peak list as
	 * a HashMap with the m/z values as keys
	 * and the corresponding intensities as
	 * values.
	 * @return
	 */
	Map<Double, Double> getPeakList();

	/**
	 * Returns the msLevel of the spectrum. NULL
	 * in case the MS level is not available or
	 * unknown.
	 * @return
	 */
	Integer getMsLevel();
	
	/**
	 * Retrieves file format specific variables
	 * as parameters. Whenever possible cvParams
	 * from the MS ontology will be used to report
	 * these additional fields.
	 * @return A ParamGroup containing the additional fields as parameters.
	 */
	Collection<? extends Param> getAdditional();

}
