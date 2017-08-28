package com.lilly.esource.fhirmapping.web.rest;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.codahale.metrics.annotation.Timed;
import com.lilly.esource.fhirmapping.beans.ObservationSearchDTO;
import com.lilly.esource.fhirmapping.beans.PatientSearchDTO;
import com.lilly.esource.fhirmapping.service.AcademicService;



/**
 * REST controller for managing academic mapping.
 *
 * <p>This class manages access to FHIR entities for Observation and Patient.</p>
 */
@RestController
@RequestMapping("/api")
public class AcademicResource {

    private final Logger log = LoggerFactory.getLogger(AcademicResource.class);

    private AcademicService academicService;

    public AcademicResource(AcademicService academicService) {
    	this.academicService = academicService;
    }

    @PostMapping("/academicMapping/observation/search")
    @Timed
    public ResponseEntity<String> searchObservations(@RequestBody ObservationSearchDTO searchDTO) throws URISyntaxException {
        log.debug("REST request to search : {}", searchDTO);

    	String searchResults = this.academicService.getAcademicObservationSearchResults(searchDTO);
    	
    	return new ResponseEntity<>(escapeXmlToJson(searchResults),  HttpStatus.OK);

    }
    
    @PostMapping("/academicMapping/patient/search")
    @Timed
    public ResponseEntity<String> searchPatients(@RequestBody PatientSearchDTO searchDTO) throws URISyntaxException {
        log.debug("REST request to search : {}", searchDTO);

    	String searchResults = this.academicService.getAcademicPatientSearchResults(searchDTO);
    	
    	return new ResponseEntity<>(escapeXmlToJson(searchResults),  HttpStatus.OK);
    	
    }
    
    private String escapeXmlToJson(String xml){  	
    	//Remove newlines since Angular freaks out otherwise
    	String escapedXml = StringUtils.escapeXml(xml.replaceAll("\\r\\n|\\r|\\n", " "));
    	String jsonResponse = "{\"ldmXml\" : " +  "\"" + escapedXml + "\"}";
    	
    	return jsonResponse;
    }
    
}
