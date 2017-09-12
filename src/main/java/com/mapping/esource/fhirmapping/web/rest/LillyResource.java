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
import com.mapping.esource.fhirmapping.service.LillyService;



/**
 * REST controller for managing Lilly LDM
 *
 * 
 */
@RestController
@RequestMapping("/api")
public class LillyResource {

    private final Logger log = LoggerFactory.getLogger(LillyResource.class);

    private LillyService lillyService;

    public LillyResource(LillyService lillyService) {
    	this.lillyService = lillyService;
    }

    /*
     * Lilly Mapping has a set of assumptions that are defined in the slideshow
     * available at the home page. This method defines an FHIR Observation search 
     */
    @PostMapping("/lillyMapping/observation/search")
    @Timed
    public ResponseEntity<String> searchObservations(@RequestBody ObservationSearchDTO searchDTO) throws URISyntaxException {
        log.debug("REST request to search : {}", searchDTO);

    	String searchResults = this.lillyService.getLillyObservationSearchResults(searchDTO);
    	
    	//Remove newlines since Angular freaks out otherwise
    	String escapedXml = StringUtils.escapeXml(searchResults.replaceAll("\\r\\n|\\r|\\n", " "));
    	String jsonResponse = "{\"ldmXml\" : " +  "\"" + escapedXml + "\"}";
    	return new ResponseEntity<>(jsonResponse,  HttpStatus.OK);
    	
    }
    
}
