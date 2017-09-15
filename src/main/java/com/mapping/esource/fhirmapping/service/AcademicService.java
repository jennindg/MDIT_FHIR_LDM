package com.mapping.esource.fhirmapping.service;

import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.dstu3.model.Procedure.ProcedureStatus;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.ResearchStudy;
import org.hl7.fhir.dstu3.model.ResearchSubject;
import org.hl7.fhir.dstu3.model.Specimen;
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
import com.mapping.esource.fhirmapping.beans.gtp.Confidential;
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
import com.mapping.esource.fhirmapping.beans.gtp.SpecimenComment;
import com.mapping.esource.fhirmapping.beans.gtp.SpecimenMaterial;
import com.mapping.esource.fhirmapping.beans.gtp.SpecimenTransport;
import com.mapping.esource.fhirmapping.beans.gtp.Study;
import com.mapping.esource.fhirmapping.beans.gtp.Subject;
import com.mapping.esource.fhirmapping.beans.gtp.TextResult;
import com.mapping.esource.fhirmapping.beans.gtp.ToxicityGrade;
import com.mapping.esource.fhirmapping.beans.gtp.Visit;
import com.mapping.esource.fhirmapping.beans.ObservationSearchDTO;
import com.mapping.esource.fhirmapping.beans.PatientSearchDTO;
import com.mapping.esource.fhirmapping.config.ApplicationProperties;
import com.mapping.esource.fhirmapping.web.rest.util.FhirUtil;
import com.sun.istack.Nullable;

/**
 * Service class for managing FHIR Academic Mapping (Target State Mapping) Services.
 */
@Service
public class AcademicService {

    private final Logger log = LoggerFactory.getLogger(AcademicService.class);

    private ApplicationProperties applicationProperties;

    public AcademicService(ApplicationProperties applicationProperties) {
    	this.applicationProperties = applicationProperties;
    }
    
    /* This method obtains Observation Search results after invoking web service methods on FHIR 
     * Server. The results from the web service calls are mapped to LDM elements.
     * 
     */
    public String getAcademicObservationSearchResults(ObservationSearchDTO searchDTO) {
    	    	
    	try{		    	
		    	GTP gtp = FhirUtil.initializeGTP(true);
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
					Patient patient = (Patient) FhirUtil.getUniqueResource(client, Patient.class, FhirUtil.getIdQuery(client, Patient.class, patientReference));
					
					ResearchSubject researchSubject = (ResearchSubject) FhirUtil.getUniqueResource(client, ResearchSubject.class, 
							FhirUtil.getParamQuery(client, ResearchSubject.class, "patient", patientReference));
					
					ResearchStudy researchStudy = (ResearchStudy) FhirUtil.getUniqueResource(client, ResearchStudy.class, FhirUtil.getIdQuery(client, ResearchStudy.class, researchSubject.getStudy().getReference()));
		
					Practitioner practitioner = (Practitioner) FhirUtil.getUniqueResource(client, Practitioner.class, FhirUtil.getIdQuery(client, Practitioner.class, researchStudy.getPrincipalInvestigator().getReference()));
		
					/**
					 * Making an assumption that there is only one location tied to a patient.
					 */
					Location location = (Location) FhirUtil.getUniqueResource(client, Location.class, FhirUtil.getIdQuery(client, Location.class, researchStudy.getSite().get(0).getReference()));
		
					Encounter encounter = (Encounter) FhirUtil.getUniqueResource(client, Encounter.class, FhirUtil.getIdQuery(client, Encounter.class, observation.getContext().getReference()));
					
					/*
					 * Making an assumption that there is only one diagnosis tied to a patient.
					 */
					Procedure procedure = (Procedure) FhirUtil.getUniqueResource(client, Procedure.class, FhirUtil.getIdQuery(client, Procedure.class, encounter.getDiagnosis().get(0).getCondition().getReference()));
		
					/*
					 * Making an assumption there is only one organization
					 */
					
					Organization organization = (Organization) FhirUtil.getUniqueResource(client, Organization.class, FhirUtil.getIdQuery(client, Organization.class, observation.getPerformer().get(0).getReference()));
		
					Specimen specimen = (Specimen) FhirUtil.getUniqueResource(client, Specimen.class, FhirUtil.getIdQuery(client, Specimen.class, observation.getSpecimen().getReference()));
		
					Study study = FhirUtil.getStudy(gtp, researchStudy.getIdentifierFirstRep().getValue());
					
					if(study.getID() == null){
			        	/*
			        	 * Assuming that there is only one identifier
			        	 */
			        	study.setID(researchStudy.getIdentifierFirstRep().getValue());
			        	study.setName(researchStudy.getTitle());
			        	/*
			        	 * Assuming Iterative
			        	 */
			        	study.setTransmissionType("I");
			        	
			        	gtp.getStudy().add(study);
					}
					
					Site site = FhirUtil.getSite(study, location.getIdentifierFirstRep().getValue());
					
					if(site.getID() == null){
			    		site.setID(location.getIdentifierFirstRep().getValue());
			    		site.setTransactionType("I");
			    		study.getSite().add(site);
					}
					

					Investigator investigator = FhirUtil.getInvestigator(study, practitioner.getIdentifierFirstRep().getValue());
					
					if(investigator.getID() == null){
						/*
						 * Assuming that there is only one identifier
						 */
						site.getInvestigator().add(investigator);
						
						investigator.setID(practitioner.getIdentifierFirstRep().getValue());
						
						String investigatorName = safeToString(practitioner.getNameFirstRep().getGivenAsSingleString()) + " " + 
													safeToString(practitioner.getNameFirstRep().getFamily()) + " " + 
													safeToString(practitioner.getNameFirstRep().getSuffixAsSingleString());
						if(StringUtils.isNotBlank(investigatorName)){
							investigator.setName(investigatorName);
						}
						
						investigator.setTransactionType("I");
					}

					
					
					Subject subject = FhirUtil.getSubject(study, patient.getIdentifierFirstRep().getValue());
					
					if(subject == null){
						
						subject = new Subject();
						// Subject.SubjectID
			            JAXBElement<String> subjectQName = factory.createSubjectID(patient.getIdentifierFirstRep().getValue());
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
			            
			            if(patient.getNameFirstRep() != null && patient.getNameFirstRep().getNameAsSingleString() != null){
			            	Confidential confidential = new Confidential();
			            	confidential.setInitials(patient.getNameFirstRep().getNameAsSingleString() != null ? patient.getNameFirstRep().getNameAsSingleString() : null);
			            	confidential.setBirthdate(patient.getBirthDate());
			            	subject.getContent().add(confidential);
			            }
					}						            
		            
		            Visit visit = FhirUtil.getVisit(study, encounter.getIdentifierFirstRep().getValue());
		            
		            if(visit == null){
		            	visit = new Visit();
			            subject.getContent().add(visit);
			            
			            visit.setID(encounter.getIdentifierFirstRep().getValue());
			            
			            /*
			             * Hardcode to Schedule 'S'
			             */
			             visit.setType("S");
		            }

		             
		             /*  /GTP/Study/Site/Investigator/Subject/Visit/Accession */
		             if(specimen.getAccessionIdentifier() != null && specimen.getAccessionIdentifier().getValue() != null){
		            	 Accession accession = new Accession();
		            	 accession.setID(specimen.getAccessionIdentifier().getValue());
		            	 visit.getAccession().add(accession);
		            	 
		                 
		                 CentralLab centralLab = new CentralLab();                              
		                 centralLab.setID(organization.getIdentifierFirstRep().getValue()); 
		                 centralLab.setName(organization.getName());
		                 accession.setCentralLab(centralLab);
		                 
		                 BaseSpecimen baseSpecimen = new BaseSpecimen();
		                 accession.getBaseSpecimen().add(baseSpecimen);
		                 
		                 baseSpecimen.setID(specimen.getIdentifierFirstRep().getValue());
		                 
		                 SpecimenCollection specimenCollection = new SpecimenCollection();
		                 if(specimen.getCollection() != null && specimen.getCollection().getCollected() instanceof DateTimeType){
		                	 specimenCollection.setActualCollectionDateTime(specimen.getCollection().getCollectedDateTimeType().getValue());
		                	 baseSpecimen.setSpecimenCollection(specimenCollection);
		                 }
		                 
		                 if(specimen.getReceivedTime() != null){
		                	 SpecimenTransport specimenTransport = new SpecimenTransport();
		                	 specimenTransport.setReceivedDateTime(specimen.getReceivedTime());
		                	 
		                	 baseSpecimen.setSpecimenTransport(specimenTransport);
		                 }
		                 
		                 if(specimen.getNoteFirstRep() != null && specimen.getNoteFirstRep().getAuthor() != null){
		                	 SpecimenComment specimenComment = new SpecimenComment();
		                	 specimenComment.setSource(specimen.getNoteFirstRep().getAuthorStringType().getValue());
		                	 specimenComment.setText(specimen.getNoteFirstRep().getText());
		                	 
		                	 baseSpecimen.getSpecimenComment().add(specimenComment);
		                 }
		                 
		                 if(specimen.getType() != null && specimen.getType().getCoding() != null && 
		                		 specimen.getType().getCoding().size() > 0){
		                	 SpecimenMaterial specimenMaterial = new SpecimenMaterial();
		                	 specimenMaterial.setID(specimen.getType().getCoding().get(0).getCode());
		                	 specimenMaterial.setName(specimen.getType().getCoding().get(0).getDisplay());
		                	 
		                	 baseSpecimen.setSpecimenMaterial(specimenMaterial);
		                 }
		                 
		                 if(procedure.getIdentifierFirstRep() != null && procedure.getIdentifierFirstRep().getValue() != null){
		                	 BaseBattery baseBattery = new BaseBattery();
		                	 baseSpecimen.getBaseBattery().add(baseBattery);
		                	 baseBattery.setID(procedure.getIdentifierFirstRep().getValue());
		                	 baseBattery.setName(procedure.getCode().getText());
		                	 
		                	 BaseTest baseTest = new BaseTest();
		                	 baseBattery.getBaseTest().add(baseTest);
		                	 
		                	 //Map Values
		                	 if(procedure.getStatus() != null){
		                		if(ProcedureStatus.COMPLETED.equals(procedure.getStatus())){
		                			baseTest.setStatus("D");
		                		}else{
		                			/*
		                			 * constraining to values D (Done) and X (Cancelled)
		                			 */
		                			baseTest.setStatus("C");
		                		}
		                	 }
		                	 
		                	 if(procedure.getPerformed() != null && procedure.getPerformed() instanceof DateTimeType){
		                		 baseTest.setTestingDateTime(procedure.getPerformedDateTimeType().getValue());	 
		                	 }
		                	 
		                	 if(procedure.getCategory().getCoding().size() > 0){
		                		 baseTest.setTestType(procedure.getCategory().getCoding().get(0).getCode());
		                	 }
		                	 
		                	 PerformingLab performingLab = new PerformingLab();
		                	 performingLab.setID(organization.getIdentifierFirstRep().getValue());
		                	 performingLab.setName(organization.getName());
		                	 
		                	 baseTest.setPerformingLab(performingLab);
		
		                 	 Optional<Coding> labTestCoding = observation.getCode().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.labTest.org")).findFirst();
		
		                 	 if(labTestCoding.isPresent()){
			                	 LabTest labTest = new LabTest();
			                	 labTest.setID(labTestCoding.get().getCode());                	 
			                	 labTest.setName(labTestCoding.get().getDisplay());
			                	 
			                	 baseTest.setLabTest(labTest);
		                 	 }
		                 	 
		                 	 Optional<Coding> receiverTestCoding = observation.getCode().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.receiverTest.org")).findFirst();
		
		                	 if(receiverTestCoding.isPresent()){
		                		 ReceiverTest receiverTest = new ReceiverTest();
		                		 receiverTest.setID(receiverTestCoding.get().getCode());
		                		 receiverTest.setName(receiverTestCoding.get().getDisplay());
		                		 
		                		 baseTest.setReceiverTest(receiverTest);             		 
		                	 }
		
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
		                		 Optional<Coding> alertFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.alert.org")).findFirst();
		                		 
		                		 if(alertFlag.isPresent()){
		                			 baseResult.setAlertFlag(alertFlag.get().getCode());            			 
		                		 }
		
		                		 Optional<Coding> deltaFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.delta.org")).findFirst();
		                		 
		                		 if(deltaFlag.isPresent()){
		                			 baseResult.setDeltaFlag(deltaFlag.get().getCode());            			 
		                		 }
		                	 
		                		 Optional<Coding> exclusionFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.exclusion.org")).findFirst();
		                		 
		                		 if(exclusionFlag.isPresent()){
		                			 baseResult.setExclusionFlag(exclusionFlag.get().getCode());            			 
		                		 }
		                		 
		                		 Optional<Coding> blindingFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.blinding.org")).findFirst();
		                		 
		                		 if(blindingFlag.isPresent()){
		                			 baseResult.setBlindingFlag(blindingFlag.get().getCode());            			 
		                		 }
		                		 
		                		 Optional<Coding> toxicityGradeCode = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.toxicity.org")).findFirst();
		                		 
		                		 if(toxicityGradeCode.isPresent()){
		                			 ToxicityGrade toxicityGrade = new ToxicityGrade();
		                			 toxicityGrade.setValue(toxicityGradeCode.get().getCode());                			 
		                			 toxicityGrade.setCodeListID(toxicityGradeCode.get().getDisplay());
		                			 
		                			 baseResult.setToxicityGrade(toxicityGrade);            			 
		                		 }
		                		 
		                		 SingleResult singleResult = new SingleResult();
		                		 baseResult.getSingleResult().add(singleResult);
		                		 
		                		 Optional<Coding> resultClass = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.resultClass.org")).findFirst();
		                		 
		                		 if(resultClass.isPresent()){
		                			singleResult.setResultClass(resultClass.get().getCode());            			 
		                		 }
		                		 
		                		 Optional<Coding> resultType = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.resultType.org")).findFirst();
		                		 
		                		 if(resultType.isPresent()){
		                			singleResult.setResultType(resultType.get().getCode());            			 
		                		 }
		                		 
		                		 
		                		 if(observation.getValue() != null && !(observation.getValue() instanceof Quantity)){
		                			 TextResult textResult = new TextResult();
		                			 textResult.setValue(observation.getValueStringType().getValueAsString());
		                			 
		                			 singleResult.setTextResult(textResult);
		                		 }		 
		                		 
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
		                 
		                 
		             } 
				}
				 return FhirUtil.convertToXml(gtp);
    	}catch(Exception e){
    		return e.getMessage();
    	}

    }

    /* This method obtains Observation Search results after invoking web service methods on FHIR 
     * Server. The results from the web service calls are mapped to LDM elements.
     * 
     */
    public String getAcademicPatientSearchResults(PatientSearchDTO searchDTO) {
    	
    	try{		    	
		    	GTP gtp = FhirUtil.initializeGTP(true);
		    	ObjectFactory factory = new ObjectFactory();
		    	
		    	
		    	FhirContext ctx = FhirContext.forDstu3();
				String serverBaseUrl = this.applicationProperties.getfHIRServerURL();
				IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);
		
				// Log requests and responses
				client.registerInterceptor(new LoggingInterceptor(true));
		    	
				IQuery<ca.uhn.fhir.model.api.Bundle>  patientQuery = FhirUtil.getPatientQuery(ctx, client, searchDTO);
				
				Bundle patientBundle = FhirUtil.getBundle(client, patientQuery);
				
				for(BundleEntryComponent entry : patientBundle.getEntry()){
					
					Patient patient = (Patient) entry.getResource();

					Bundle observationBundle =  FhirUtil.getBundle(client, 
							FhirUtil.getParamQuery(client, Observation.class, "patient", patient.getIdElement().getIdPart()));
					
					for(BundleEntryComponent observationEntry : observationBundle.getEntry()){
					     
							Observation observation = (Observation) observationEntry.getResource();
					     
							ResearchSubject researchSubject = (ResearchSubject) FhirUtil.getUniqueResource(client, ResearchSubject.class, 
									FhirUtil.getParamQuery(client, ResearchSubject.class, "patient", patient.getIdElement().getIdPart()));
							
							ResearchStudy researchStudy = (ResearchStudy) FhirUtil.getUniqueResource(client, ResearchStudy.class, FhirUtil.getIdQuery(client, ResearchStudy.class, researchSubject.getStudy().getReference()));
				
							Practitioner practitioner = (Practitioner) FhirUtil.getUniqueResource(client, Practitioner.class, FhirUtil.getIdQuery(client, Practitioner.class, researchStudy.getPrincipalInvestigator().getReference()));
				
							/**
							 * Making an assumption that there is only one location tied to a patient.
							 */
							Location location = (Location) FhirUtil.getUniqueResource(client, Location.class, FhirUtil.getIdQuery(client, Location.class, researchStudy.getSite().get(0).getReference()));
				
							Encounter encounter = (Encounter) FhirUtil.getUniqueResource(client, Encounter.class, FhirUtil.getIdQuery(client, Encounter.class, observation.getContext().getReference()));
							
							/*
							 * Making an assumption that there is only one diagnosis tied to a patient.
							 */
							Procedure procedure = (Procedure) FhirUtil.getUniqueResource(client, Procedure.class, FhirUtil.getIdQuery(client, Procedure.class, encounter.getDiagnosis().get(0).getCondition().getReference()));
				
							/*
							 * Making an assumption there is only one organization
							 */
							
							Organization organization = (Organization) FhirUtil.getUniqueResource(client, Organization.class, FhirUtil.getIdQuery(client, Organization.class, observation.getPerformer().get(0).getReference()));
				
							Specimen specimen = (Specimen) FhirUtil.getUniqueResource(client, Specimen.class, FhirUtil.getIdQuery(client, Specimen.class, observation.getSpecimen().getReference()));
				
							Study study = FhirUtil.getStudy(gtp, researchStudy.getIdentifierFirstRep().getValue());
							
							if(study.getID() == null){
					        	/*
					        	 * Assuming that there is only one identifier
					        	 */
					        	study.setID(researchStudy.getIdentifierFirstRep().getValue());
					        	study.setName(researchStudy.getTitle());
					        	/*
					        	 * Assuming Iterative
					        	 */
					        	study.setTransmissionType("I");
					        	
					        	gtp.getStudy().add(study);
							}
							
							Site site = FhirUtil.getSite(study, location.getIdentifierFirstRep().getValue());
							
							if(site.getID() == null){
					    		site.setID(location.getIdentifierFirstRep().getValue());
					    		site.setTransactionType("I");
					    		study.getSite().add(site);
							}
							

							Investigator investigator = FhirUtil.getInvestigator(study, practitioner.getIdentifierFirstRep().getValue());
							
							if(investigator.getID() == null){
								/*
								 * Assuming that there is only one identifier
								 */
								site.getInvestigator().add(investigator);
								
								investigator.setID(practitioner.getIdentifierFirstRep().getValue());
								
								String investigatorName = safeToString(practitioner.getNameFirstRep().getGivenAsSingleString()) + " " + 
															safeToString(practitioner.getNameFirstRep().getFamily()) + " " + 
															safeToString(practitioner.getNameFirstRep().getSuffixAsSingleString());
								if(StringUtils.isNotBlank(investigatorName)){
									investigator.setName(investigatorName);
								}
								
								investigator.setTransactionType("I");
							}

							
							
							Subject subject = FhirUtil.getSubject(study, patient.getIdentifierFirstRep().getValue());
							
							if(subject == null){
								
								subject = new Subject();
								// Subject.SubjectID
					            JAXBElement<String> subjectQName = factory.createSubjectID(patient.getIdentifierFirstRep().getValue());
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
					            
					            if(patient.getNameFirstRep() != null && patient.getNameFirstRep().getNameAsSingleString() != null){
					            	Confidential confidential = new Confidential();
					            	confidential.setInitials(patient.getNameFirstRep().getNameAsSingleString() != null ? patient.getNameFirstRep().getNameAsSingleString() : null);
					            	confidential.setBirthdate(patient.getBirthDate());
					            	subject.getContent().add(confidential);
					            }
							}						            
				            
				            Visit visit = FhirUtil.getVisit(study, encounter.getIdentifierFirstRep().getValue());
				            
				            if(visit == null){
				            	visit = new Visit();
					            subject.getContent().add(visit);
					            
					            visit.setID(encounter.getIdentifierFirstRep().getValue());
					            
					            /*
					             * Hardcode to Schedule 'S'
					             */
					             visit.setType("S");
				            }

				             
				             /*  /GTP/Study/Site/Investigator/Subject/Visit/Accession */
				             if(specimen.getAccessionIdentifier() != null && specimen.getAccessionIdentifier().getValue() != null){
				            	 Accession accession = new Accession();
				            	 accession.setID(specimen.getAccessionIdentifier().getValue());
				            	 visit.getAccession().add(accession);
				            	 
				                 
				                 CentralLab centralLab = new CentralLab();                              
				                 centralLab.setID(organization.getIdentifierFirstRep().getValue()); 
				                 centralLab.setName(organization.getName());
				                 accession.setCentralLab(centralLab);
				                 
				                 BaseSpecimen baseSpecimen = new BaseSpecimen();
				                 accession.getBaseSpecimen().add(baseSpecimen);
				                 
				                 baseSpecimen.setID(specimen.getIdentifierFirstRep().getValue());
				                 
				                 SpecimenCollection specimenCollection = new SpecimenCollection();
				                 if(specimen.getCollection() != null && specimen.getCollection().getCollected() instanceof DateTimeType){
				                	 specimenCollection.setActualCollectionDateTime(specimen.getCollection().getCollectedDateTimeType().getValue());
				                	 baseSpecimen.setSpecimenCollection(specimenCollection);
				                 }
				                 
				                 if(specimen.getReceivedTime() != null){
				                	 SpecimenTransport specimenTransport = new SpecimenTransport();
				                	 specimenTransport.setReceivedDateTime(specimen.getReceivedTime());
				                	 
				                	 baseSpecimen.setSpecimenTransport(specimenTransport);
				                 }
				                 
				                 if(specimen.getNoteFirstRep() != null && specimen.getNoteFirstRep().getAuthor() != null){
				                	 SpecimenComment specimenComment = new SpecimenComment();
				                	 specimenComment.setSource(specimen.getNoteFirstRep().getAuthorStringType().getValue());
				                	 specimenComment.setText(specimen.getNoteFirstRep().getText());
				                	 
				                	 baseSpecimen.getSpecimenComment().add(specimenComment);
				                 }
				                 
				                 if(specimen.getType() != null && specimen.getType().getCoding() != null && 
				                		 specimen.getType().getCoding().size() > 0){
				                	 SpecimenMaterial specimenMaterial = new SpecimenMaterial();
				                	 specimenMaterial.setID(specimen.getType().getCoding().get(0).getCode());
				                	 specimenMaterial.setName(specimen.getType().getCoding().get(0).getDisplay());
				                	 
				                	 baseSpecimen.setSpecimenMaterial(specimenMaterial);
				                 }
				                 
				                 if(procedure.getIdentifierFirstRep() != null && procedure.getIdentifierFirstRep().getValue() != null){
				                	 BaseBattery baseBattery = new BaseBattery();
				                	 baseSpecimen.getBaseBattery().add(baseBattery);
				                	 baseBattery.setID(procedure.getIdentifierFirstRep().getValue());
				                	 baseBattery.setName(procedure.getCode().getText());
				                	 
				                	 BaseTest baseTest = new BaseTest();
				                	 baseBattery.getBaseTest().add(baseTest);
				                	 
				                	 //Map Values
				                	 if(procedure.getStatus() != null){
				                		if(ProcedureStatus.COMPLETED.equals(procedure.getStatus())){
				                			baseTest.setStatus("D");
				                		}else{
				                			/*
				                			 * constraining to values D (Done) and X (Cancelled)
				                			 */
				                			baseTest.setStatus("C");
				                		}
				                	 }
				                	 
				                	 if(procedure.getPerformed() != null && procedure.getPerformed() instanceof DateTimeType){
				                		 baseTest.setTestingDateTime(procedure.getPerformedDateTimeType().getValue());	 
				                	 }
				                	 
				                	 if(procedure.getCategory().getCoding().size() > 0){
				                		 baseTest.setTestType(procedure.getCategory().getCoding().get(0).getCode());
				                	 }
				                	 
				                	 PerformingLab performingLab = new PerformingLab();
				                	 performingLab.setID(organization.getIdentifierFirstRep().getValue());
				                	 performingLab.setName(organization.getName());
				                	 
				                	 baseTest.setPerformingLab(performingLab);
				
				                 	 Optional<Coding> labTestCoding = observation.getCode().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.labTest.org")).findFirst();
				
				                 	 if(labTestCoding.isPresent()){
					                	 LabTest labTest = new LabTest();
					                	 labTest.setID(labTestCoding.get().getCode());                	 
					                	 labTest.setName(labTestCoding.get().getDisplay());
					                	 
					                	 baseTest.setLabTest(labTest);
				                 	 }
				                 	 
				                 	 Optional<Coding> receiverTestCoding = observation.getCode().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.receiverTest.org")).findFirst();
				
				                	 if(receiverTestCoding.isPresent()){
				                		 ReceiverTest receiverTest = new ReceiverTest();
				                		 receiverTest.setID(receiverTestCoding.get().getCode());
				                		 receiverTest.setName(receiverTestCoding.get().getDisplay());
				                		 
				                		 baseTest.setReceiverTest(receiverTest);             		 
				                	 }
				
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
				                		 Optional<Coding> alertFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.alert.org")).findFirst();
				                		 
				                		 if(alertFlag.isPresent()){
				                			 baseResult.setAlertFlag(alertFlag.get().getCode());            			 
				                		 }
				
				                		 Optional<Coding> deltaFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.delta.org")).findFirst();
				                		 
				                		 if(deltaFlag.isPresent()){
				                			 baseResult.setDeltaFlag(deltaFlag.get().getCode());            			 
				                		 }
				                	 
				                		 Optional<Coding> exclusionFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.exclusion.org")).findFirst();
				                		 
				                		 if(exclusionFlag.isPresent()){
				                			 baseResult.setExclusionFlag(exclusionFlag.get().getCode());            			 
				                		 }
				                		 
				                		 Optional<Coding> blindingFlag = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.blinding.org")).findFirst();
				                		 
				                		 if(blindingFlag.isPresent()){
				                			 baseResult.setBlindingFlag(blindingFlag.get().getCode());            			 
				                		 }
				                		 
				                		 Optional<Coding> toxicityGradeCode = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.toxicity.org")).findFirst();
				                		 
				                		 if(toxicityGradeCode.isPresent()){
				                			 ToxicityGrade toxicityGrade = new ToxicityGrade();
				                			 toxicityGrade.setValue(toxicityGradeCode.get().getCode());                			 
				                			 toxicityGrade.setCodeListID(toxicityGradeCode.get().getDisplay());
				                			 
				                			 baseResult.setToxicityGrade(toxicityGrade);            			 
				                		 }
				                		 
				                		 SingleResult singleResult = new SingleResult();
				                		 baseResult.getSingleResult().add(singleResult);
				                		 
				                		 Optional<Coding> resultClass = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.resultClass.org")).findFirst();
				                		 
				                		 if(resultClass.isPresent()){
				                			singleResult.setResultClass(resultClass.get().getCode());            			 
				                		 }
				                		 
				                		 Optional<Coding> resultType = observation.getCategoryFirstRep().getCoding().stream().filter(coding -> coding.getSystem().contains("http://mock.resultType.org")).findFirst();
				                		 
				                		 if(resultType.isPresent()){
				                			singleResult.setResultType(resultType.get().getCode());            			 
				                		 }
				                		 
				                		 
				                		 if(observation.getValue() != null && !(observation.getValue() instanceof Quantity)){
				                			 TextResult textResult = new TextResult();
				                			 textResult.setValue(observation.getValueStringType().getValueAsString());
				                			 
				                			 singleResult.setTextResult(textResult);
				                		 }		 
				                		 
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
				                 
				                 
				             } 
					     
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
