package com.fhir.ldm.mapping.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Resource;

import com.fhir.ldm.mapping.beans.gtp.Accession;
import com.fhir.ldm.mapping.beans.gtp.BaseBattery;
import com.fhir.ldm.mapping.beans.gtp.BaseSpecimen;
import com.fhir.ldm.mapping.beans.gtp.GTP;
import com.fhir.ldm.mapping.beans.gtp.Investigator;
import com.fhir.ldm.mapping.beans.gtp.Site;
import com.fhir.ldm.mapping.beans.gtp.Study;
import com.fhir.ldm.mapping.beans.gtp.Subject;
import com.fhir.ldm.mapping.beans.gtp.TransmissionSource;
import com.fhir.ldm.mapping.beans.gtp.Visit;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

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
	    
	 public static Accession getAccession(Study study, String accessionId) {
			
	    	for(Site site : study.getSite()){
	    			for(Investigator investigator : site.getInvestigator()){
	    				for(Subject subject : investigator.getSubject()){
	        				if(subject.getContent() != null){
	        					for(Object object : subject.getContent()){
	        						if (object instanceof Visit){
	        							Visit visit = (Visit) object;
	        							for(Object accessionObject : visit.getAccession()) {
	        								Accession accession = (Accession)accessionObject;
		        							if(accession.getID().equalsIgnoreCase(accessionId)){
		        								return accession;       								
		        							}	        								
	        							}
	        						}
	        					}
	        				}
	    				}
	    			}
	    	}
	    	
	    	return null;
		}
	 
	 public static BaseSpecimen getBaseSpecimen(Study study, String baseSpecimenId) {
			
	    	for(Site site : study.getSite()){
	    			for(Investigator investigator : site.getInvestigator()){
	    				for(Subject subject : investigator.getSubject()){
	        				if(subject.getContent() != null){
	        					for(Object object : subject.getContent()){
	        						if (object instanceof Visit){
	        							Visit visit = (Visit) object;
	        							for(Object accessionObject : visit.getAccession()) {
	        								Accession accession = (Accession)accessionObject;
	        								for(Object baseSpecimenObject : accession.getBaseSpecimen()) {
	        									BaseSpecimen baseSpecimen = (BaseSpecimen)baseSpecimenObject;
			        							if(baseSpecimen.getID().equalsIgnoreCase(baseSpecimenId)){
			        								return baseSpecimen;       								
			        							}	        								
	        								}
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
		
	    public static BaseBattery getBaseBattery(Study study, String baseBatteryId) {
				
		    	for(Site site : study.getSite()){
		    			for(Investigator investigator : site.getInvestigator()){
		    				for(Subject subject : investigator.getSubject()){
		        				if(subject.getContent() != null){
		        					for(Object object : subject.getContent()){
		        						if (object instanceof Visit){
		        							Visit visit = (Visit) object;
		        							for(Object accessionObject : visit.getAccession()) {
		        								Accession accession = (Accession)accessionObject;
		        								for(Object baseSpecimenObject : accession.getBaseSpecimen()) {
		        									BaseSpecimen baseSpecimen = (BaseSpecimen)baseSpecimenObject;
		        									for(Object baseBatteryObject : baseSpecimen.getBaseBattery()) {
		        										BaseBattery baseBattery = (BaseBattery)baseBatteryObject;
					        							if(baseBattery.getID().equalsIgnoreCase(baseBatteryId)){
					        								return baseBattery;       								
					        							}	        								

		        									}
		        								}
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
	    
		public static String validateXml(String xml, Schema schema) throws Exception{
			
			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBContext context = JAXBContext.newInstance(GTP.class);
			Unmarshaller um = context.createUnmarshaller();
			um.setSchema(schema);
			try {
				um.unmarshal(source);
			} catch (Exception e) {
				return e.getCause().getMessage();
			}

			return "";

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
		
	    public static Resource getUniqueResource(IGenericClient client, Class<? extends IBaseResource> classType, IQuery<IBaseBundle> query) throws Exception{
				    	
	    	Bundle bundle = query
					.prettyPrint()
					.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
					.execute();
			
			
			if(bundle == null || bundle.getEntry().size() == 0){
				throw new Exception("Could not obtain Bundle for Query "+query.toString());
			}
			
			return bundle.getEntry().get(0).getResource();

		}
	    
	    public static Bundle getBundle(IGenericClient client, IQuery<IBaseBundle> query) throws Exception{
				    	
	    	Bundle bundle = query
					.prettyPrint()
					.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
					.execute();
			
	    	fetchRestOfBundle(client, bundle);	
			
			if(bundle == null || bundle.getEntry().size() == 0){
				throw new Exception("Could not obtain Bundle for Query "+query.toString());
			}
			
			return bundle;

		}
	    
		public static IQuery<IBaseBundle> getIdQuery(IGenericClient client, Class<? extends IBaseResource> classType, String id){

			ca.uhn.fhir.rest.gclient.IQuery<IBaseBundle> query = client.search().forResource(classType);
			query.and(new TokenClientParam("_id").exactly().code(id));
			
			return query;
		}

		public static IQuery<IBaseBundle> getParamQuery(IGenericClient client, Class<? extends IBaseResource> classType, String param, String paramValue){

			IQuery<IBaseBundle> query = client.search().forResource(classType);
			query.and(new TokenClientParam(param).exactly().code(paramValue));
			
			return query;
		}
		
		public static IQuery<IBaseBundle> getParamsQuery(IGenericClient client, Class<? extends IBaseResource> classType, String[] params, String[] paramValues){

			IQuery<IBaseBundle> query = client.search().forResource(classType);
			
			int i = 0;
			for(String param : params) {
				query.and(new TokenClientParam(param).exactly().code(paramValues[i]));
				i++;
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
	    
	    public static void tryManually() throws Exception {
	        /*CloseableHttpClient httpclient = HttpClients.createDefault();
	        try {
	        	//HttpHost proxy = new HttpHost(<your_proxy_addess>, <port>);
	        	//RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

	            HttpGet httpget = new HttpGet("https://api-v5-r4.hspconsortium.org/R4ClinicalResearch/open/metadata");

	            System.out.println("Executing request " + httpget.getRequestLine());

	            // Create a custom response handler
	            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	                @Override
	                public String handleResponse(
	                        final HttpResponse response) throws ClientProtocolException, IOException {
	                    int status = response.getStatusLine().getStatusCode();
	                    if (status >= 200 && status < 300) {
	                        HttpEntity entity = response.getEntity();
	                        return entity != null ? EntityUtils.toString(entity) : null;
	                    } else {
	                        throw new ClientProtocolException("Unexpected response status: " + status);
	                    }
	                }

	            };
	            String responseBody = httpclient.execute(httpget, responseHandler);
	            System.out.println("----------------------------------------");
	            System.out.println(responseBody);
	        } finally {
	            httpclient.close();
	        }*/
	    }

}
