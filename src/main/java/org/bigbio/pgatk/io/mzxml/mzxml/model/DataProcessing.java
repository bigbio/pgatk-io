
package org.bigbio.pgatk.io.mzxml.mzxml.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://sashimi.sourceforge.net/schema_revision/mzXML_3.2}software"/&gt;
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="processingOperation" type="{http://sashimi.sourceforge.net/schema_revision/mzXML_3.2}namevalueType"/&gt;
 *           &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="intensityCutoff" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="centroided" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="deisotoped" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="chargeDeconvoluted" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="spotIntegration" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "software",
    "processingOperationAndComment"
})
public class DataProcessing
    implements Serializable, MzXMLObject
{

    private final static long serialVersionUID = 320L;
    @XmlElement(required = true)
    protected Software software;
    @XmlElements({
        @XmlElement(name = "comment", type = String.class),
        @XmlElement(name = "processingOperation", type = NameValue.class)
    })
    protected List<Serializable> processingOperationAndComment;
    @XmlAttribute
    protected Float intensityCutoff;
    @XmlAttribute
    protected Boolean centroided;
    @XmlAttribute
    protected Boolean deisotoped;
    @XmlAttribute
    protected Boolean chargeDeconvoluted;
    @XmlAttribute
    protected Boolean spotIntegration;

    /**
     * Software used to convert the data. If data has been processed (e.g. profile &gt; centroid) by any additional progs these should be added too.
     * 
     * @return
     *     possible object is
     *     {@link Software }
     *     
     */
    public Software getSoftware() {
        return software;
    }

    /**
     * Sets the value of the software property.
     * 
     * @param value
     *     allowed object is
     *     {@link Software }
     *     
     */
    public void setSoftware(Software value) {
        this.software = value;
    }

    /**
     * Gets the value of the processingOperationAndComment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processingOperationAndComment property.
     * 
     * <p>
     * For example, to put a new item, do as follows:
     * <pre>
     *    getProcessingOperationAndComment().put(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * {@link NameValue }
     * 
     * 
     */
    public List<Serializable> getProcessingOperationAndComment() {
        if (processingOperationAndComment == null) {
            processingOperationAndComment = new ArrayList<>();
        }
        return this.processingOperationAndComment;
    }

    /**
     * Gets the value of the intensityCutoff property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getIntensityCutoff() {
        return intensityCutoff;
    }

    /**
     * Sets the value of the intensityCutoff property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setIntensityCutoff(Float value) {
        this.intensityCutoff = value;
    }

    /**
     * Gets the value of the centroided property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCentroided() {
        return centroided;
    }

    /**
     * Sets the value of the centroided property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCentroided(Boolean value) {
        this.centroided = value;
    }

    /**
     * Gets the value of the deisotoped property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeisotoped() {
        return deisotoped;
    }

    /**
     * Sets the value of the deisotoped property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeisotoped(Boolean value) {
        this.deisotoped = value;
    }

    /**
     * Gets the value of the chargeDeconvoluted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isChargeDeconvoluted() {
        return chargeDeconvoluted;
    }

    /**
     * Sets the value of the chargeDeconvoluted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChargeDeconvoluted(Boolean value) {
        this.chargeDeconvoluted = value;
    }

    /**
     * Gets the value of the spotIntegration property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSpotIntegration() {
        return spotIntegration;
    }

    /**
     * Sets the value of the spotIntegration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSpotIntegration(Boolean value) {
        this.spotIntegration = value;
    }

}
