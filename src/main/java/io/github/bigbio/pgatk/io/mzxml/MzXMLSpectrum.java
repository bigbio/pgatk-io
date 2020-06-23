package io.github.bigbio.pgatk.io.mzxml;

import io.github.bigbio.pgatk.io.common.CvParam;
import io.github.bigbio.pgatk.io.mzxml.mzxml.model.Scan;
import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import javax.xml.datatype.Duration;
import java.util.*;

/**
 * This class wraps Scan elements into PeakListParser
 * compatible Spectrum objects. It is implemented as
 * a nested class to be completely independent of the
 * JAXB generated object model.
 * @author jg
 *
 */
public class MzXMLSpectrum implements Spectrum {
	/**
	 * The scan id
	 */
	private String id;
	/**
	 * The spectrum's charge
	 */
	private Integer charge;
	/**
	 * The precursor's m/z
	 */
	private Double precursorMz;
	/**
	 * The precursor's intensity
	 */
	private Double precursorIntensity;
	/**
	 * The actual peak list
	 */
	private Map<Double, Double> peakList;
	/**
	 * ParamGroup holding additional information
	 * about the spectrum.
	 */
	private List<CvParam> paramGroup = new ArrayList<>();
	/**
	 * The spectrum's ms level
	 */
	private int msLevel;

	private Long index;

	private String retentionTime;

	/**
	 * Default Spectrum for mzXML scans
	 */
	public MzXMLSpectrum(){}

	/**
	 * Create a new MzXMLSpectrum object wrapping
	 * the given Scan object.
	 * @param scan the Scan to create a MzXMLSpectra object from.
	 */
	public MzXMLSpectrum(Scan scan) throws MzXMLParsingException {
		// make sure it's a MS2 spectrum
//		if (scan.getMsLevel().intValue() != 2)
//			throw new MzXMLParsingException("Unsupported MS level encountered in MzXMLSpectrum: MzXMLSpectrum can only wrap MS2 spectra.");
		
		// only single peak lists are supported by this class
		if (scan.getPeaks().size() == 1)
			peakList = MzXMLIndexedReader.convertPeaksToMap(scan.getPeaks().get(0));
		else
			throw new MzXMLParsingException("Multiple peak lists can not be modeled in a mzXMLSpectrum.");
		
		msLevel = Math.toIntExact(scan.getMsLevel());
		
		// set the num
		id = String.valueOf(scan.getNum());
		
		// set the rest of the information
		if (scan.getPrecursorMz().size() == 1) {
			precursorMz = (double) scan.getPrecursorMz().get(0).getValue();
			precursorIntensity = (double) scan.getPrecursorMz().get(0).getPrecursorIntensity();
			charge = Math.toIntExact(scan.getPrecursorMz().get(0).getPrecursorCharge());
		}
		
		if (scan.getPolarity() != null)
			setPolarity(scan.getPolarity());
		if (scan.getScanType() != null)
			setScanType(scan.getScanType());
		if (scan.getFilterLine() != null)
			setFilterLine(scan.getFilterLine());
		if (scan.isCentroided() != null && scan.isCentroided())
			setCentroid(scan.isCentroided());
		if (scan.isDeisotoped() != null && scan.isDeisotoped())
			setDeisotoped(scan.isDeisotoped());
		if (scan.isChargeDeconvoluted())
			setChargeDeconvoluted();
		if (scan.getRetentionTime() != null)
			setRetentionTime(scan.getRetentionTime());
		if (scan.getIonisationEnergy() != null)
			setIonizationEnergy(scan.getIonisationEnergy());
		if (scan.getCollisionEnergy() != null)
			setCollitionEnergy(scan.getCollisionEnergy());
		if (scan.getCidGasPressure() != null)
			setCidgasPressure(scan.getCidGasPressure());
		if (scan.getTotIonCurrent() != null)
			setIonCurrent(scan.getTotIonCurrent());
	}

	public void setIonCurrent(Float totIonCurrent) {
		paramGroup.add(new CvParam("total ion current", totIonCurrent.toString(), "MS", "MS:1000285"));
	}


	public void setCidgasPressure(Float cidGasPressure) {
		paramGroup.add(new CvParam("collision gas pressure", cidGasPressure.toString(), "MS", "MS:1000045"));
	}

	public void setCollitionEnergy(Float collisionEnergy) {
		paramGroup.add(new CvParam("collision energy", collisionEnergy.toString(), "MS", "MS:1000045"));
	}

	public void setIonizationEnergy(Float ionisationEnergy) {
		paramGroup.add(new CvParam("ionisation energy", ionisationEnergy.toString(), null, null));
	}


	public void setRetentionTime(Duration retentionTime) {
		this.retentionTime = retentionTime.toString();
	}

	public void setRetentionTime(String retentionTime) {
		paramGroup.add(new CvParam("retention time", retentionTime, "MS", "MS:1000894"));
	}


	public void setChargeDeconvoluted() {
		paramGroup.add(new CvParam("charge deconvolution", "true", "MS", "MS:1000034"));
	}

	public void setDeisotoped(Boolean deisotoped) {
		paramGroup.add(new CvParam("deisotoping", "true", "MS", "MS:1000033"));
	}


	public void setCentroid(Boolean centroid) {
		paramGroup.add(new CvParam("centroid spectrum", "true", "MS", "MS:1000127"));
	}

	public void setFilterLine(String filterLine) {
		paramGroup.add(new CvParam("filter line", filterLine, null, null));
	}


	@Override
	public Long getIndex() {
		return index;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer getPrecursorCharge() {
		return charge;
	}

	@Override
	public Double getPrecursorMZ() {
		return precursorMz;
	}

	@Override
	public Double getPrecursorIntensity() {
		return precursorIntensity;
	}

	@Override
	public Map<Double, Double> getPeakList() {
		return peakList;
	}

	@Override
	public Integer getMsLevel() {
		// these are always MS2 spectra
		return msLevel;
	}

	@Override
	public Collection<CvParam> getAdditional() {
		return paramGroup;
	}


	public void setIndex(Long index) {
		this.index = index;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMsLevel(int msLevel) {
		this.msLevel = msLevel;
	}

	public void setPolarity(String polarity) {
		paramGroup.add(new CvParam("scan polarity", polarity, "MS", "MS:1000465"));
	}

	private void setScanType(String scanType) {
		paramGroup.add(new CvParam("scan type", scanType, null, null));
	}

	public void setPrecursorMz(Double valueOf) {
		this.precursorMz = valueOf;
	}

	public void setPrecursorCharge(int charge) {
		this.charge = charge;
	}

	public void setPrecursorIntesity(double intensity) {
		this.precursorIntensity = intensity;
	}

	public void setActivationMethod(String activationMethod) {
		paramGroup.add(new CvParam("activation method", activationMethod, null, null));
	}

	public void setPeaks(Map<Double, Double> peaks) {
		this.peakList = peaks;
	}

	/**
	 * Compiles all the information from this Ms2Query object.
	 * @return a String of all the information from this object.
	 */
	@Override
	public String toString() {
		StringBuilder query = new StringBuilder("BEGIN IONS\n");
		// process the optional attributes
		if (charge != null) {
			query.append("CHARGE=").append(charge).append('\n');
		}

		if (retentionTime != null) {
			query.append("RTINSECONDS=").append(retentionTime).append('\n');
		}

		query.append(paramGroup.toString());

		List<Double> masses = new ArrayList<>(peakList.keySet());
		Collections.sort(masses);
		for (Double mz : masses) {
			query.append(mz).append(' ').append(peakList.get(mz)).append('\n');
		}
		query.append("END IONS\n");
		return query.toString();
	}

}
