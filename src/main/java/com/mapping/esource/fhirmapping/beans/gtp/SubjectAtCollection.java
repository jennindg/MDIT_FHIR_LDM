//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.12 at 09:11:55 AM EDT 
//


package com.mapping.esource.fhirmapping.beans.gtp;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.mapping.esource.fhirmapping.util.DoubleAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="AgeAtCollection">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="AgeUnits" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="6"/>
 *             &lt;enumeration value="Y"/>
 *             &lt;enumeration value="M"/>
 *             &lt;enumeration value="D"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="FastingStatus">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="7"/>
 *             &lt;enumeration value="Y"/>
 *             &lt;enumeration value="N"/>
 *             &lt;enumeration value="U"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "SubjectAtCollection")
public class SubjectAtCollection
    implements Serializable
{

    private final static long serialVersionUID = 5190875983846359311L;
    @XmlAttribute(name = "AgeAtCollection")
    @XmlJavaTypeAdapter(DoubleAdapter.class)
    protected Double ageAtCollection;
    @XmlAttribute(name = "AgeUnits", required = true)
    protected String ageUnits;
    @XmlAttribute(name = "FastingStatus")
    protected String fastingStatus;

    /**
     * Gets the value of the ageAtCollection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getAgeAtCollection() {
        return ageAtCollection;
    }

    /**
     * Sets the value of the ageAtCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgeAtCollection(Double value) {
        this.ageAtCollection = value;
    }

    /**
     * Gets the value of the ageUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgeUnits() {
        return ageUnits;
    }

    /**
     * Sets the value of the ageUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgeUnits(String value) {
        this.ageUnits = value;
    }

    /**
     * Gets the value of the fastingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFastingStatus() {
        return fastingStatus;
    }

    /**
     * Sets the value of the fastingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFastingStatus(String value) {
        this.fastingStatus = value;
    }

}
