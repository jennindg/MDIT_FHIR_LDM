//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.15 at 01:50:20 PM EDT 
//


package com.mapping.esource.fhirmapping.beans.gtp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.mapping.esource.fhirmapping.util.DateTimeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CentralLab"/>
 *         &lt;element ref="{}BaseSpecimen" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}TransactionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="20"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="LastActiveDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "centralLab",
    "baseSpecimen",
    "transactionType"
})
@XmlRootElement(name = "Accession")
public class Accession
    implements Serializable
{

    private final static long serialVersionUID = 5190875983846359311L;
    @XmlElement(name = "CentralLab", required = true)
    protected CentralLab centralLab;
    @XmlElement(name = "BaseSpecimen")
    protected List<BaseSpecimen> baseSpecimen;
    @XmlElement(name = "TransactionType")
    protected String transactionType;
    @XmlAttribute(name = "ID")
    protected String id;
    @XmlAttribute(name = "LastActiveDateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name = "dateTime")
    protected Date lastActiveDateTime;

    /**
     * Gets the value of the centralLab property.
     * 
     * @return
     *     possible object is
     *     {@link CentralLab }
     *     
     */
    public CentralLab getCentralLab() {
        return centralLab;
    }

    /**
     * Sets the value of the centralLab property.
     * 
     * @param value
     *     allowed object is
     *     {@link CentralLab }
     *     
     */
    public void setCentralLab(CentralLab value) {
        this.centralLab = value;
    }

    /**
     * Gets the value of the baseSpecimen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the baseSpecimen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBaseSpecimen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BaseSpecimen }
     * 
     * 
     */
    public List<BaseSpecimen> getBaseSpecimen() {
        if (baseSpecimen == null) {
            baseSpecimen = new ArrayList<BaseSpecimen>();
        }
        return this.baseSpecimen;
    }

    /**
     * Gets the value of the transactionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionType(String value) {
        this.transactionType = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the lastActiveDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getLastActiveDateTime() {
        return lastActiveDateTime;
    }

    /**
     * Sets the value of the lastActiveDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastActiveDateTime(Date value) {
        this.lastActiveDateTime = value;
    }

}
