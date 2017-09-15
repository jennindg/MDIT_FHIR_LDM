package com.mapping.esource.fhirmapping.web.rest;

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
import com.mapping.esource.fhirmapping.beans.ObservationSearchDTO;
import com.mapping.esource.fhirmapping.service.CurrentStateService;



/**
 * REST controller for managing Current State LDM
 *
 * 
 */
@RestController
@RequestMapping("/api")
public class CurrentStateResource {

    private final Logger log = LoggerFactory.getLogger(CurrentStateResource.class);

    private CurrentStateService currentStateService;

    public CurrentStateResource(CurrentStateService currentStateService) {
    	this.currentStateService = currentStateService;
    }

    /*
     * Current State Mapping has a set of assumptions that are defined in the slideshow
     * available at the home page. This method defines an FHIR Observation search 
     */
    @PostMapping("/currentStateMapping/observation/search")
    @Timed
    public ResponseEntity<String> searchObservations(@RequestBody ObservationSearchDTO searchDTO) throws URISyntaxException {
        log.debug("REST request to search : {}", searchDTO);

    	String searchResults = this.currentStateService.getCurrentStateObservationSearchResults(searchDTO);
    	
    	//Remove newlines since Angular freaks out otherwise
    	String escapedXml = StringUtils.escapeXml(searchResults.replaceAll("\\r\\n|\\r|\\n", " "));
    	String jsonResponse = "{\"ldmXml\" : " +  "\"" + escapedXml + "\"}";
    	return new ResponseEntity<>(jsonResponse,  HttpStatus.OK);
    	
    }
    
}
