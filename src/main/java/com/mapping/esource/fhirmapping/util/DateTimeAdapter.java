package com.mapping.esource.fhirmapping.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is used by JAXB to override its default date formatting when
 * Marshaling and unmarshalling XML.
 * 
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {

	private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		
		@Override
		protected SimpleDateFormat initialValue() {
			//Default Timezone since I do not want to change current behaviour
			SimpleDateFormat sdf = new SimpleDateFormat(LdmGlobalConstants.LDM_DEFAULT_DATETIME_FORMAT);
			return sdf;
		}
	};
	/*
	 * Override of the default JAXB marshal method for marshaling Date objects
	 * to a String for XML. This one uses the above SimpleDateFormat.
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Date v) {
		
		if (v == null) {
			return null;
		} else {
		    
		    String result = dateFormat.get().format(v);
		    if (result.length()>2) {
		        result = new StringBuilder(result).insert(result.length()-2, ":").toString();
		    }
			return result;
		}
	}

	/*
	 * Override of the default JAXB marshal method for marshalling Date objects
	 * to a String for XML. This one uses the above SimpleDateFormat.
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Date unmarshal(String v) throws ParseException {

		if (v == null) {
			return null;
		} else {
			return dateFormat.get().parse(v);
		}
	}
}