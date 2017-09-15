package com.mapping.esource.fhirmapping.service;

import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.IQuery;

import com.mapping.esource.fhirmapping.beans.gtp.Accession;
import com.mapping.esource.fhirmapping.beans.gtp.BaseBattery;
import com.mapping.esource.fhirmapping.beans.gtp.BaseResult;
import com.mapping.esource.fhirmapping.beans.gtp.BaseSpecimen;
import com.mapping.esource.fhirmapping.beans.gtp.BaseTest;
import com.mapping.esource.fhirmapping.beans.gtp.CentralLab;
import com.mapping.esource.fhirmapping.beans.gtp.GTP;
import com.mapping.esource.fhirmapping.beans.gtp.Investigator;
import com.mapping.esource.fhirmapping.beans.gtp.LOINCTestCode;
import com.mapping.esource.fhirmapping.beans.gtp.LabTest;
import com.mapping.esource.fhirmapping.beans.gtp.NumericResult;
import com.mapping.esource.fhirmapping.beans.gtp.ObjectFactory;
import com.mapping.esource.fhirmapping.beans.gtp.PerformingLab;
import com.mapping.esource.fhirmapping.beans.gtp.ReceiverTest;
import com.mapping.esource.fhirmapping.beans.gtp.ResultReferenceRange;
import com.mapping.esource.fhirmapping.beans.gtp.ResultUnits;
import com.mapping.esource.fhirmapping.beans.gtp.Sex;
import com.mapping.esource.fhirmapping.beans.gtp.SingleResult;
import com.mapping.esource.fhirmapping.beans.gtp.Site;
import com.mapping.esource.fhirmapping.beans.gtp.SpecimenCollection;
import com.mapping.esource.fhirmapping.beans.gtp.Study;
import com.mapping.esource.fhirmapping.beans.gtp.Subject;
import com.mapping.esource.fhirmapping.beans.gtp.Visit;
import com.mapping.esource.fhirmapping.beans.ObservationSearchDTO;
import com.mapping.esource.fhirmapping.config.ApplicationProperties;
import com.mapping.esource.fhirmapping.web.rest.util.FhirUtil;
import com.sun.istack.Nullable;

/**
 * Service class for managing FHIR Current State Mapping Services.
 */
@Service
public class CurrentStateService {

    private final Logger log = LoggerFactory.getLogger(CurrentStateService.class);

    private ApplicationProperties applicationProperties;

    public CurrentStateService(ApplicationProperties applicationProperties) {
    	this.applicationProperties = applicationProperties;
    }

    /* This method obtains Observation Search results after invoking web service methods on FHIR 
     * Server. The results from the web service calls are mapped to LDM elements.
     * 
     */
    public String getCurrentStateObservationSearchResults(ObservationSearchDTO searchDTO) {
    	
    	try{
    		    // Get Transmission Source
		    	GTP gtp = FhirUtil.initializeGTP(false);
		    	ObjectFactory factory = new ObjectFactory();
		    	
		    	
		    	FhirContext ctx = FhirContext.forDstu3();
				String serverBaseUrl = this.applicationProperties.getfHIRServerURL();
				IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);
		
				// Log requests and responses
				client.registerInterceptor(new LoggingInterceptor(true));
		    	
				IQuery<ca.uhn.fhir.model.api.Bundle>  observationQuery = FhirUtil.getObservationQuery(ctx, client, searchDTO);
				
				Bundle observationBundle = FhirUtil.getBundle(client, observationQuery);
				
				for(BundleEntryComponent entry : observationBundle.getEntry()){
					
					Observation observation = (Observation) entry.getResource();
					 
					String patientReference = observation.getSubject().getReference();
					
					//Split by / and take the last part
					String[] idTokens = patientReference.split("/");
					
					if(idTokens == null || idTokens.length == 0){
						throw new Exception("Subject Reference not valid for Fhir LDM Mapping");
					}
					patientReference = idTokens[idTokens.length -1];
					Patient patient = (Patient) FhirUtil.getUniqueResource(client, Patient.class, FhirUtil.getIdQuery(client, Patient.class, patientReference));
					
					String[] ids = patientReference.split("-");
					
					if(ids == null || ids.length != 3){
						throw new Exception("Subject Reference not valid for Fhir LDM Mapping");
					}
		
					Study study = FhirUtil.getStudy(gtp, ids[0]);
					
					if(study.getID() == null){
			        	study.setID(ids[0]);
			        	/*
			        	 * Assuming Iterative
			        	 */
			        	study.setTransmissionType("I");
			        	
			        	gtp.getStudy().add(study);
					}
					
					Site site = FhirUtil.getSite(study, ids[1]);
					
					if(site.getID() == null){
			    		site.setID(ids[1]);
			    		site.setTransactionType("I");
			    		study.getSite().add(site);
					}
					
					//Assume that Site Id is the equivalent
					Investigator investigator = FhirUtil.getInvestigator(study, site.getID());
					
					if(investigator.getID() == null){
						
						site.getInvestigator().add(investigator);
						
						investigator.setID(site.getID());						
						
						investigator.setTransactionType("I");
					}
									
					Subject subject = FhirUtil.getSubject(study, ids[2]);
					
					if(subject == null){
						
						subject = new Subject();
						// Subject.SubjectID
			            JAXBElement<String> subjectQName = factory.createSubjectID(ids[2]);
			            subject.getContent().add(subjectQName);
						/*
						 * Only one subject
						 */
						investigator.getSubject().add(subject);
						
			            if(patient.getGender() != null && patient.getGender().getDisplay() != null){
			            	Sex sex = new Sex();
			            	if("Male".equalsIgnoreCase(patient.getGender().getDisplay())){
			            		sex.setValue("M");
			            	}else{
			            		/*
			            		 * Since LDM only supports "M" and "F", the values are constrained to them
			            		 */
			            		sex.setValue("F");
			            	}
			            	subject.getContent().add(sex);
			            }
			            
					}						            
		            
					//Hardcode to 0994
		            Visit visit = FhirUtil.getVisit(study, "00944");
		            
		            if(visit == null){
		            	visit = new Visit();
			            subject.getContent().add(visit);
			            
			            visit.setID("00944");
			            
			            /*
			             * Hardcode to Schedule 'S'
			             */
			             visit.setType("S");
		            }

	             
		            /*  /GTP/Study/Site/Investigator/Subject/Visit/Accession */
	            	 Accession accession = new Accession();
	            	 visit.getAccession().add(accession);
	            	 
	                 
	                 CentralLab centralLab = new CentralLab();  
	                 //Hardcode
	                 centralLab.setID("1111"); 
	                 accession.setCentralLab(centralLab);
	                 
	                 BaseSpecimen baseSpecimen = new BaseSpecimen();
	                 accession.getBaseSpecimen().add(baseSpecimen);
	                 
	                 //As per Hui, use the effectiveDateTime
	                 if(observation.getEffective() != null && observation.getEffective() instanceof DateTimeType){
		                 SpecimenCollection specimenCollection = new SpecimenCollection();
	                	 specimenCollection.setActualCollectionDateTime(observation.getEffectiveDateTimeType().getValue());
	                	 baseSpecimen.setSpecimenCollection(specimenCollection);
	                 }
                	 
	                 
                	 BaseBattery baseBattery = new BaseBattery();
                	 baseSpecimen.getBaseBattery().add(baseBattery);
                	 //TO DO...Get from DB
                	 baseBattery.setID("CHEM");
                	 
                	 BaseTest baseTest = new BaseTest();
                	 baseBattery.getBaseTest().add(baseTest);
                	 //Default to Done for status

            		 baseTest.setStatus("D");	                			
                	 
	                 if(observation.getEffective() != null && observation.getEffective() instanceof DateTimeType){
                		 baseTest.setTestingDateTime(observation.getEffectiveDateTimeType().getValue());	 
	                 }			                
                	 
                	 PerformingLab performingLab = new PerformingLab();
                	 //TO DO. Get from DB
                	 performingLab.setID("123");
                	 
                	 baseTest.setPerformingLab(performingLab);

                	 //Assuming that there is only one name-value pair available
                	 if(observation.getCode() != null && observation.getCode().getText() != null){
	                	 LabTest labTest = new LabTest();
	                	 labTest.setID(observation.getCode().getText());                	 
	                	 
	                	 baseTest.setLabTest(labTest);
                	 }

                	 //TO DO.. Get Value from DB
                 	 ReceiverTest receiverTest = new ReceiverTest();
                	 receiverTest.setID("123");
                		 
                	 baseTest.setReceiverTest(receiverTest);             		 

                 	 Optional<Coding> loincTestCoding = observation.getCode().getCoding().stream().filter(coding -> coding.getSystem().contains("http://loinc.org")).findFirst();

                	 if(loincTestCoding.isPresent()){
                		 LOINCTestCode loincTestCode = new LOINCTestCode();
                		 loincTestCode.setValue(loincTestCoding.get().getCode());
                		 loincTestCode.setCodeListID(loincTestCoding.get().getSystem());
                		 
                		 baseTest.setLOINCTestCode(loincTestCode);
                	 }
                	 
                	 BaseResult baseResult = new BaseResult();
                	 baseTest.setBaseResult(baseResult);
                	 
                	 if(observation.getStatus() != null){
                		 if("FINAL".equalsIgnoreCase(observation.getStatus().name())){
		                	 baseResult.setReportedResultStatus("F");
                		 }else{
		                	 baseResult.setReportedResultStatus("P");		                			 
                		 }
                	 }
                	 
                	 if(observation.getCategory().size() > 0){
                		 
                		 //TO DO...Get Value from DB
                		 baseResult.setBlindingFlag("B"); 
                		 		                		 
                		 SingleResult singleResult = new SingleResult();
                		 baseResult.getSingleResult().add(singleResult);
                		 
                		 //Default to R
                		 singleResult.setResultClass("R");            			 		                		 		                		 
                		 singleResult.setResultType("T");            			 
                		 		                		 	 
                		 
                		 if(observation.getValue() != null && (observation.getValue() instanceof Quantity)){
                			 NumericResult numericResult = new NumericResult();
                			 numericResult.setValue(observation.getValueQuantity().getValue().doubleValue());
                			 numericResult.setPrecision(String.valueOf(observation.getValueQuantity().getValue()));
                			 
                			 singleResult.setNumericResult(numericResult);
                		 }
                		 
                		 if(observation.getReferenceRangeFirstRep() != null){
                			 ResultReferenceRange range = new ResultReferenceRange();
                			 String highRange = safeToString(observation.getReferenceRangeFirstRep().getHigh().getValue()) + " " + 
                					 			safeToString(observation.getReferenceRangeFirstRep().getHigh().getUnit());
                			 String lowRange = safeToString(observation.getReferenceRangeFirstRep().getLow().getValue()) + " "+ 
                					 			safeToString(observation.getReferenceRangeFirstRep().getLow().getUnit());
                			 
                			 if(StringUtils.isNotBlank(highRange)){
                				 range.setReferenceRangeHigh(highRange);
                			 }
                			 
                			 if(StringUtils.isNotBlank(lowRange)){
                				 range.setReferenceRangeLow(lowRange);
                			 }
                			 
                			 if(range.getReferenceRangeHigh() != null || range.getReferenceRangeLow() != null){
                				 singleResult.setResultReferenceRange(range);
                			 }
                		 }	 
                		 
                		 if(observation.getValueQuantity() != null && observation.getValueQuantity().getUnit() != null){
                			 ResultUnits resultUnits = new ResultUnits();
                			 resultUnits.setValue(observation.getValueQuantity().getUnit());
                			 resultUnits.setCodeListID(observation.getValueQuantity().getSystem());
                			 
                			 singleResult.setResultUnits(resultUnits);
                		 }
                		 
                	 }
                	 
                	 if(observation.getEffectiveDateTimeType() != null && observation.getEffectiveDateTimeType().getValue() != null){
                			 baseResult.setReportedDateTime(observation.getEffectiveDateTimeType().getValue());
                	 }		                	                	 	                 
	                 
				}
				 return FhirUtil.convertToXml(gtp);
    	}catch(Exception e){
    		return e.getMessage();
    	}

    }

	/** Returns {@code obj.toString()}, or {@code ""} if {@code obj} is {@code null}. */
    private static String safeToString(@Nullable Object obj) {
      return obj == null ? "" : obj.toString();
    }
	
}
