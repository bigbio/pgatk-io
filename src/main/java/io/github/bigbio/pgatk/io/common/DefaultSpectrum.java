package io.github.bigbio.pgatk.io.common;

import io.github.bigbio.pgatk.io.common.spectra.Spectrum;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultSpectrum implements Spectrum, Serializable {

	private static final long serialVersionUID = 1L;
	private final String id;
	private final Long index;
	private final int precursorCharge;
	private final Double precursorMz;
	private final Double precursorIntensity;
	private final Map<Double, Double> peakList;
	private final Integer msLevel;
	private final List<CvParam> params;


	public DefaultSpectrum(String id, long index, int precursorCharge,
						   double precursorMz, double precursorIntensity,
						   Map<Double, Double> peakList, Integer msLevel, List<CvParam> params ) {
		this.id = id;
		this.index = index;
		this.precursorCharge = precursorCharge;
		this.precursorMz = precursorMz;
		this.precursorIntensity = precursorIntensity;
		this.peakList = peakList;
		this.msLevel = msLevel;
		this.params = params;
	}



	@Override
	public Long getIndex() {
		return index;
	}

	@Override
	public String getId() {
		return id;
	}

	public Integer getPrecursorCharge() {
		return precursorCharge;
	}

	public Double getPrecursorMZ() {
		return precursorMz;
	}

	public Double getPrecursorIntensity() {
		return precursorIntensity;
	}

	public Map<Double, Double> getPeakList() {
		return peakList;
	}

	public Integer getMsLevel() {
		return msLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((peakList == null) ? 0 : peakList.hashCode());
		result = prime * result
				+ ((precursorCharge == -1) ? 0 : precursorCharge);
		result = prime
				* result
				+ ((precursorIntensity == -1) ? 0 : precursorIntensity
						.hashCode());
		result = prime * result
				+ ((precursorMz == null) ? 0 : precursorMz.hashCode());
		result = prime * result
				+ ((msLevel == null) ? 0 : msLevel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultSpectrum other = (DefaultSpectrum) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (peakList == null) {
			if (other.peakList != null)
				return false;
		} else if (!peakList.equals(other.peakList))
			return false;
		if (precursorCharge == -1) {
			if (other.precursorCharge != -1)
				return false;
		} else if (!(precursorCharge == other.precursorCharge))
			return false;
		if (precursorIntensity == null) {
			if (other.precursorIntensity != null)
				return false;
		} else if (!precursorIntensity.equals(other.precursorIntensity))
			return false;
		if (precursorMz == null) {
			if (other.precursorMz != null)
				return false;
		} else if (!precursorMz.equals(other.precursorMz))
			return false;
		if (msLevel == null) {
            return other.msLevel == null;
		} else return msLevel.equals(other.msLevel);
	}

	@Override
	public Collection<? extends Param> getAdditional() {
		return params;
	}
}
