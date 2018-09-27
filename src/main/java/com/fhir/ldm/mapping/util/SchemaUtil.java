package com.fhir.ldm.mapping.util;

import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * Util class for helping Schema creation
 */
public class SchemaUtil {

	private Schema schema = null;
	
	public Schema getSchema() throws Exception{
		
		if(null == schema) {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			URL url = getClass().getClassLoader().getResource("LabResultsBaseSchema_lilly_V1_0.xsd");
			schema = schemaFactory.newSchema(url);
		}
		
		return schema;
	}
	
    

}
