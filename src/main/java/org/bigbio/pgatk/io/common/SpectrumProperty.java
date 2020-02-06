package org.bigbio.pgatk.io.common;

/**
 * This class storage the properties for a single spectrum such as Retention time,
 * mz value, etc.
 *
 * @author ypriverol
 */
public class SpectrumProperty {

    // The key is the combination of spectrumId + propertyId
    String key;

    // Spectrum Identifier
    String spectrumId;

    // Property Name (e.g. RT, precursorMZ)
    String propertyId;

    //Value of the property
    String value;

    public SpectrumProperty() { }

    public SpectrumProperty(String key, String spectrumId, String propertyId, String value) {
        this.key = key;
        this.spectrumId = spectrumId;
        this.propertyId = propertyId;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(String spectrumId) {
        this.spectrumId = spectrumId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
