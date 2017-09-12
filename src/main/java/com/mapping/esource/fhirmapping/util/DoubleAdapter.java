package com.mapping.esource.fhirmapping.util;

import java.text.DecimalFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is used by JAXB to override its default date formatting when
 * Marshaling and unmarshalling XML.
 * 
 */
public class DoubleAdapter extends XmlAdapter<String, Double> {

	/*
	 * Override of the default JAXB marshal method for marshaling Date objects
	 * to a String for XML. This one uses the above SimpleDateFormat.
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public Double unmarshal(String v) throws NumberFormatException {
	    
	    if (v==null) { 
	        return null; 
	    }
	    
		return Double.valueOf(v);
	}

	/*
	 * Override of the default JAXB marshal method for marshalling Date objects
	 * to a String for XML. This one uses the above SimpleDateFormat.
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Double v) throws IllegalArgumentException {

		DecimalFormat numberFormatter = new DecimalFormat("###.#####");
		String result = null;
		if (v!=null) { 
		    result = numberFormatter.format(v); 
		}
		
		return result;
	}
}