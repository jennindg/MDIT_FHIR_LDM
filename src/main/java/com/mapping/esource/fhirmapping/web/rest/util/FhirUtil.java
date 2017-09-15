package com.mapping.esource.fhirmapping.web.rest.util;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

import com.mapping.esource.fhirmapping.beans.gtp.GTP;
import com.mapping.esource.fhirmapping.beans.gtp.Investigator;
import com.mapping.esource.fhirmapping.beans.gtp.Site;
import com.mapping.esource.fhirmapping.beans.gtp.Study;
import com.mapping.esource.fhirmapping.beans.gtp.Subject;
import com.mapping.esource.fhirmapping.beans.gtp.TransmissionSource;
import com.mapping.esource.fhirmapping.beans.gtp.Visit;
import com.mapping.esource.fhirmapping.beans.ObservationSearchDTO;
import com.mapping.esource.fhirmapping.beans.PatientSearchDTO;

/**
 * Util class for helping mapping of FHIR to LDM.
 */
public class FhirUtil {

    public static final String MODEL_VERSION = "01-0-00";
    public static final String CURRENT_TRANSMISSION_SOURCE = "00001";
    public static final String ACADEMIC_TRANSMISSION_SOURCE = "00994";
    
	 public static Visit getVisit(Study study, String visitId) {
			
	    	for(Site site : study.getSite()){
	    			for(Investigator investigator : site.getInvestigator()){
	    				for(Subject subject : investigator.getSubject()){
	        				if(subject.getContent() != null){
	        					for(Object object : subject.getContent()){
	        						if (object instanceof Visit){
	        							Visit visit = (Visit) object;
	        							if(visit.getID().equalsIgnoreCase(visitId)){
	        								return visit;       								
	        							}
	        						}
	        					}
	        				}
	    				}
	    			}
	    	}
	    	
	    	return null;
		}
	    
	    public static Subject getSubject(Study study, String subjectId) {
			
	  	  for(Site site : study.getSite()){
	    			for(Investigator investigator : site.getInvestigator()){
	    				for(Subject subject : investigator.getSubject()){
	        				if(subject.getContent() != null){
	        					for(Object object : subject.getContent()){
	        						if (object instanceof JAXBElement<?>){
	        							JAXBElement element = (JAXBElement) object;
	        							if( element.getDeclaredType().equals(String.class) && element.getName().getLocalPart().equalsIgnoreCase("SubjectID") &&
	        									((String) element.getValue()).equalsIgnoreCase(subjectId)){
	        								return subject;
	        								
	        							}
	        						}
	        					}
	        				}
	    				}
	    			}
	    	}
	    	
	    	return null;
		}

	    public static Investigator getInvestigator(Study study, String id) {
			 	
	    	for(Site site : study.getSite()){
	    			for(Investigator investigator : site.getInvestigator()){
	    				if(investigator.getID().equalsIgnoreCase(id)){
	    					return investigator;
	    				}
	    			}
	    	}    	
	    	return new Investigator();
		}

	    public static  Site getSite(Study study, String siteId) {
			
	   		for(Site site : study.getSite()){
	    		if(site.getID().equalsIgnoreCase(siteId)){
	    				return site;
	    		}
	    	}
	    	
	    	return new Site();
		}
	    
	    public static Study getStudy(GTP gtp, String studyId){
	    	
	    	for(Study study : gtp.getStudy()){
	    		if(study.getID().equalsIgnoreCase(studyId)){
	    			return study;
	    		}
	    	}
	    	
	    	return new Study();
	    }
	    
	    
		public static String convertToXml(GTP gtp) throws Exception{
	    	
			JAXBContext context = JAXBContext.newInstance(GTP.class);
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 

			StringWriter sw = new StringWriter();
			m.marshal(gtp, sw);
			
			return sw.toString();

	    }
	    
		public static GTP initializeGTP(boolean academic){
	    	GTP gtp = new GTP();
	    	gtp.setModelVersion(MODEL_VERSION);
	    	gtp.setCreationDateTime(new Date());
	    	
	    	TransmissionSource transmissionSource = new TransmissionSource();
	    	transmissionSource.setID(academic ? ACADEMIC_TRANSMISSION_SOURCE : CURRENT_TRANSMISSION_SOURCE);

	    	gtp.setTransmissionSource(transmissionSource);
	    	
	    	return gtp;
	    	
	    }
		
	    public static Resource getUniqueResource(IGenericClient client, Class<? extends IBaseResource> classType, IQuery<ca.uhn.fhir.model.api.Bundle> query) throws Exception{
			
	    	Bundle bundle = query
					.prettyPrint()
					.returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
					.execute();
			
			
			if(bundle == null || bundle.getTotal() != 1 || bundle.getEntry().size() == 0){
				throw new Exception("Could not obtain Bundle for Query "+query.toString());
			}
			
			return bundle.getEntry().get(0).getResource();

		}
	    
	    public static Bundle getBundle(IGenericClient client, IQuery<ca.uhn.fhir.model.api.Bundle> query) throws Exception{
			
	    	Bundle bundle = query
					.prettyPrint()
					.returnBundle(org.hl7.fhir.dstu3.model.Bundle.class)
					.execute();
			
	    	fetchRestOfBundle(client, bundle);	
			
			if(bundle == null || bundle.getEntry().size() == 0){
				throw new Exception("Could not obtain Bundle for Query "+query.toString());
			}
			
			return bundle;

		}
	    
		public static IQuery<ca.uhn.fhir.model.api.Bundle> getIdQuery(IGenericClient client, Class<? extends IBaseResource> classType, String id){

			ca.uhn.fhir.rest.gclient.IQuery<ca.uhn.fhir.model.api.Bundle> query = client.search().forResource(classType);
			query.and(new TokenClientParam("_id").exactly().code(id));
			
			return query;
		}

		public static IQuery<ca.uhn.fhir.model.api.Bundle> getParamQuery(IGenericClient client, Class<? extends IBaseResource> classType, String param, String paramValue){

			ca.uhn.fhir.rest.gclient.IQuery<ca.uhn.fhir.model.api.Bundle> query = client.search().forResource(classType);
			query.and(new TokenClientParam(param).exactly().code(paramValue));
			
			return query;
		}
		
		public static IQuery<ca.uhn.fhir.model.api.Bundle> getObservationQuery(FhirContext ctx, IGenericClient client, ObservationSearchDTO searchDTO){
	    	
			ca.uhn.fhir.rest.gclient.IQuery<ca.uhn.fhir.model.api.Bundle> query = client.search().forResource(Observation.class);
			
			if(StringUtils.isNotBlank(searchDTO.getId())){
				query.and(new TokenClientParam("_id").exactly().code(searchDTO.getId()));
			}
			
			if(StringUtils.isNotBlank(searchDTO.getStartDate())){
				query.and(Observation.DATE.afterOrEquals().day(searchDTO.getStartDate()));
			}
			
			if(StringUtils.isNotBlank(searchDTO.getEndDate())){
				query.and(Observation.DATE.beforeOrEquals().day(searchDTO.getEndDate()));
			}
			
			return query;
			
	    }
		
		public static IQuery<ca.uhn.fhir.model.api.Bundle> getPatientQuery(FhirContext ctx, IGenericClient client, PatientSearchDTO searchDTO){
	    	
			ca.uhn.fhir.rest.gclient.IQuery<ca.uhn.fhir.model.api.Bundle> query = client.search().forResource(Patient.class);
			
			if(StringUtils.isNotBlank(searchDTO.getId())){
				query.and(new TokenClientParam("_id").exactly().code(searchDTO.getId()));
			}
			
			if(StringUtils.isNotBlank(searchDTO.getStartDate())){
				query.and(Patient.BIRTHDATE.afterOrEquals().day(searchDTO.getStartDate()));
			}
			
			if(StringUtils.isNotBlank(searchDTO.getEndDate())){
				query.and(Patient.BIRTHDATE.beforeOrEquals().day(searchDTO.getEndDate()));
			}
			
			return query;
			
	    }
		
		public static void fetchRestOfBundle(IGenericClient theClient, Bundle theBundle) {

	        Set<String> resourcesAlreadyAdded = new HashSet<String>();
	        addInitialUrlsToSet(theBundle, resourcesAlreadyAdded);
	        Bundle partialBundle = theBundle;
	        for (;;) {
	            if (partialBundle.getLink(IBaseBundle.LINK_NEXT) != null) {
	                partialBundle = theClient.loadPage().next(partialBundle).execute();
	                addAnyResourcesNotAlreadyPresentToBundle(theBundle, partialBundle, resourcesAlreadyAdded);
	            } else {
	                break;
	            }
	        }

	        theBundle.getLink().clear();
	    }

		public static void addAnyResourcesNotAlreadyPresentToBundle(Bundle theAggregatedBundle, Bundle thePartialBundle, Set<String> theResourcesAlreadyAdded) {
	        for (BundleEntryComponent entry : thePartialBundle.getEntry()) {
	            if (!theResourcesAlreadyAdded.contains(entry.getFullUrl())) {
	                theResourcesAlreadyAdded.add(entry.getFullUrl());
	                theAggregatedBundle.getEntry().add(entry);
	            }
	        }
	    }
	    public static void addInitialUrlsToSet(Bundle theBundle, Set<String> theResourcesAlreadyAdded) {
	        for (BundleEntryComponent entry : theBundle.getEntry()) {
	            theResourcesAlreadyAdded.add(entry.getFullUrl());
	        }
	    }
}
