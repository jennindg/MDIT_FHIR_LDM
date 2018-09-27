package com.fhir.ldm.mapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResearchStudy;
import org.hl7.fhir.r4.model.ResearchSubject;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;

import com.fhir.ldm.mapping.beans.gtp.Accession;
import com.fhir.ldm.mapping.beans.gtp.BaseBattery;
import com.fhir.ldm.mapping.beans.gtp.BaseResult;
import com.fhir.ldm.mapping.beans.gtp.BaseSpecimen;
import com.fhir.ldm.mapping.beans.gtp.BaseTest;
import com.fhir.ldm.mapping.beans.gtp.CentralLab;
import com.fhir.ldm.mapping.beans.gtp.Confidential;
import com.fhir.ldm.mapping.beans.gtp.GTP;
import com.fhir.ldm.mapping.beans.gtp.Investigator;
import com.fhir.ldm.mapping.beans.gtp.LOINCTestCode;
import com.fhir.ldm.mapping.beans.gtp.LabTest;
import com.fhir.ldm.mapping.beans.gtp.NumericResult;
import com.fhir.ldm.mapping.beans.gtp.ObjectFactory;
import com.fhir.ldm.mapping.beans.gtp.PerformingLab;
import com.fhir.ldm.mapping.beans.gtp.Race;
import com.fhir.ldm.mapping.beans.gtp.ReceiverTest;
import com.fhir.ldm.mapping.beans.gtp.ResultReferenceRange;
import com.fhir.ldm.mapping.beans.gtp.ResultUnits;
import com.fhir.ldm.mapping.beans.gtp.Sex;
import com.fhir.ldm.mapping.beans.gtp.SingleResult;
import com.fhir.ldm.mapping.beans.gtp.Site;
import com.fhir.ldm.mapping.beans.gtp.SpecimenCollection;
import com.fhir.ldm.mapping.beans.gtp.SpecimenComment;
import com.fhir.ldm.mapping.beans.gtp.SpecimenMaterial;
import com.fhir.ldm.mapping.beans.gtp.SpecimenTransport;
import com.fhir.ldm.mapping.beans.gtp.Study;
import com.fhir.ldm.mapping.beans.gtp.Subject;
import com.fhir.ldm.mapping.beans.gtp.SubjectAtCollection;
import com.fhir.ldm.mapping.beans.gtp.TextResult;
import com.fhir.ldm.mapping.beans.gtp.ToxicityGrade;
import com.fhir.ldm.mapping.beans.gtp.Visit;
import com.fhir.ldm.mapping.util.FhirUtil;
import com.fhir.ldm.mapping.util.SchemaUtil;
import com.sun.istack.Nullable;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class Main {


	public static void main(String[] args) throws URISyntaxException, IOException, java.text.ParseException {

		OptionReader or = new OptionReader(args);

    	try{		    
    		SchemaUtil schemaUtil = new SchemaUtil();
	    	GTP gtp = FhirUtil.initializeGTP(true);
	    	ObjectFactory factory = new ObjectFactory();
	    	
	    	
	    	FhirContext ctx = FhirContext.forR4();
			String serverBaseUrl = or.serverBase;		
			IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);
	
			IQuery<IBaseBundle>  researchSubjectQuery = getResearchSubjectQuery(ctx, client, or);

			Bundle researchSubjectBundle = FhirUtil.getBundle(client, researchSubjectQuery);
			
			for(BundleEntryComponent entry : researchSubjectBundle.getEntry()){
				
				ResearchSubject researchSubject = (ResearchSubject) entry.getResource();
				 
				Patient patient = (Patient) FhirUtil.getUniqueResource(client, Patient.class, FhirUtil.getIdQuery(client, Patient.class, researchSubject.getIndividual().getReference()));
								
				ResearchStudy researchStudy = (ResearchStudy) FhirUtil.getUniqueResource(client, ResearchStudy.class, FhirUtil.getIdQuery(client, ResearchStudy.class, researchSubject.getStudy().getReference()));
	
				Practitioner practitioner = (Practitioner) FhirUtil.getUniqueResource(client, Practitioner.class, FhirUtil.getIdQuery(client, Practitioner.class, researchStudy.getPrincipalInvestigator().getReference()));
						
				Organization organization = (Organization) FhirUtil.getUniqueResource(client, Organization.class, FhirUtil.getIdQuery(client, Organization.class, patient.getManagingOrganization().getReference()));
	
				Bundle observationBundle =  FhirUtil.getBundle(client, 
						FhirUtil.getParamQuery(client, Observation.class, "patient", patient.getIdElement().getIdPart()));

				for(BundleEntryComponent observationEntry : observationBundle.getEntry()){

					Observation observation = (Observation) observationEntry.getResource();

					Specimen specimen = (Specimen) FhirUtil.getUniqueResource(client, Specimen.class, FhirUtil.getIdQuery(client, Specimen.class, observation.getSpecimen().getReference()));
		
					Study study = FhirUtil.getStudy(gtp, researchStudy.getIdentifierFirstRep().getValue());
					
					Encounter encounter = (Encounter) FhirUtil.getUniqueResource(client, Encounter.class, FhirUtil.getIdQuery(client, Encounter.class, observation.getContext().getReference()));
					
					String[] params = new String[] {"patient", "encounter"};
					String[] paramValues = new String[] {researchSubject.getIndividual().getReference(), observation.getContext().getReference()};
					DiagnosticReport diagnosisReport = (DiagnosticReport) FhirUtil.getUniqueResource(client, DiagnosticReport.class, 
							FhirUtil.getParamsQuery(client, DiagnosticReport.class, params, paramValues));
					
					ServiceRequest serviceRequest = (ServiceRequest) FhirUtil.getUniqueResource(client, ServiceRequest.class, 
							FhirUtil.getParamQuery(client, ServiceRequest.class, "patient", researchSubject.getIndividual().getReference()));
					
					if(study.getID() == null){

						for(Identifier identifier : researchStudy.getIdentifier()) {
							if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0
								&& identifier.getAssigner().getReference().equals(organization.getIdentifierFirstRep().getAssigner().getReference())) {
								study.setID(identifier.getValue());
								break;
							}
						}
						
			        	study.setName(researchStudy.getTitle());
			        	/*
			        	 * Assuming Iterative
			        	 */
			        	study.setTransmissionType("I");
			        	
			        	gtp.getStudy().add(study);
					}
					
					Site site = FhirUtil.getSite(study, organization.getIdentifierFirstRep().getValue());
					
					//TO DO
					if(site.getID() == null && IdentifierUse.SECONDARY.compareTo(organization.getIdentifierFirstRep().getUse()) == 0){
			    		site.setID(organization.getIdentifierFirstRep().getValue());
			    		site.setTransactionType("I");
			    		study.getSite().add(site);
					}
					
					String investigatorId = null;
					for(Identifier identifier : practitioner.getIdentifier()) {
						if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0
							&& identifier.getAssigner().getReference().equals(organization.getIdentifierFirstRep().getAssigner().getReference())) {
							investigatorId = identifier.getValue();
							break;
						}
					}
	
					Investigator investigator = FhirUtil.getInvestigator(study, investigatorId);
					
					if(investigator.getID() == null){
						/*
						 * Assuming that there is only one identifier
						 */
						site.getInvestigator().add(investigator);
						
						investigator.setID(investigatorId);
						
						String investigatorName = safeToString(practitioner.getNameFirstRep().getFamily()) + ", " +
												   safeToString(practitioner.getNameFirstRep().getGivenAsSingleString());
						
						if(StringUtils.isNotBlank(investigatorName)){
							investigator.setName(investigatorName);
						}
						
						investigator.setTransactionType("I");
					}
	
					
					String subjectId = null;
					for(Identifier identifier : researchSubject.getIdentifier()) {
						if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0
							&& identifier.getAssigner().getReference().equals(organization.getIdentifierFirstRep().getAssigner().getReference())) {
							subjectId = identifier.getValue();
							break;
						}
					}

					Subject subject = FhirUtil.getSubject(study, subjectId);
					
					if(subject == null){
						
						subject = new Subject();
						// Subject.SubjectID
			            JAXBElement<String> subjectQName = factory.createSubjectID(subjectId);
			            subject.getContent().add(subjectQName);
			            
						// Subject.ScreenID
			            JAXBElement<String> screenQName = factory.createScreenID(subjectId);
			            subject.getContent().add(screenQName);
	
						/*
						 * Only one subject
						 */
						investigator.getSubject().add(subject);
						
			            if(patient.getGender() != null && patient.getGender().getDisplay() != null){
			            	Sex sex = new Sex();
			            	//TO DO Mapping of Gender
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
			            //Get subject Race
			            List<Extension> subjectRaceExts = patient.getExtensionsByUrl("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race");
			            
			            if(subjectRaceExts != null && subjectRaceExts.size() > 0) {
			            	CodeableConcept codeableConcept = (CodeableConcept) subjectRaceExts.get(0).getValue();
			            	if(codeableConcept.getCodingFirstRep() != null && codeableConcept.getCodingFirstRep().getCode() != null) {
			            		Race race = new Race();
			            		race.setCodeListID(codeableConcept.getCodingFirstRep().getCode());
			            		race.setValue(codeableConcept.getCodingFirstRep().getDisplay());
				            	subject.getContent().add(race);
			            	}
			            }
			            
			            if(patient.getNameFirstRep() != null && patient.getNameFirstRep().getNameAsSingleString() != null){
			            	Confidential confidential = new Confidential();
			            	confidential.setInitials(patient.getNameFirstRep().getNameAsSingleString() != null ? getInitials(patient.getNameFirstRep().getNameAsSingleString()) : null);
			            	confidential.setBirthdate(patient.getBirthDate());
			            	subject.getContent().add(confidential);
			            }
					}						            
		            
					String visitId = null;
					for(Identifier identifier : encounter.getIdentifier()) {
						if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0
							&& identifier.getAssigner().getReference().equals(organization.getIdentifierFirstRep().getAssigner().getReference())) {
							visitId = identifier.getValue();
							break;
						}
					}

		            Visit visit = FhirUtil.getVisit(study, visitId);
		            
		            if(visit == null){
		            	visit = new Visit();
			            subject.getContent().add(visit);
			            
			            visit.setID(visitId);
			            visit.setTypeModifier(encounter.getTypeFirstRep().getText());
	
			            List<Extension> encounterExtns = encounter.getExtensionsByUrl("https://fhirtest.uhn.ca/baseDstu3/StructureDefinition/Encounter-Name");
			            if(encounterExtns != null && encounterExtns.size() > 0) {
			            	visit.setName(encounterExtns.get(0).getValue().toString());
			            }
	
			            //If Visit Name contains Unscheduled, then visit is unscheduled
			            if(visit.getName() != null && visit.getName().toUpperCase().contains("UNSCHEDULED")) {
			            	visit.setType("U");
			            }else {
			            	visit.setType("S");
			            }
		            }
	
		             
		             /*  /GTP/Study/Site/Investigator/Subject/Visit/Accession */
		             if(specimen.getAccessionIdentifier() != null && specimen.getAccessionIdentifier().getValue() != null){
		            	 Accession accession = FhirUtil.getAccession(study, specimen.getAccessionIdentifier().getValue());
		            	 
		            	 if(accession == null) {
		            		 accession = new Accession();
			            	 accession.setID(specimen.getAccessionIdentifier().getValue());
			            	 visit.getAccession().add(accession);
			            	 
			                 
			                 CentralLab centralLab = new CentralLab();
							 for(Identifier identifier : organization.getIdentifier()) {
								 if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0) {
									centralLab.setID(identifier.getValue());
									break;
								 }
							 }

			                 centralLab.setName(organization.getName());
			                 accession.setCentralLab(centralLab);
			                 
			                 BaseSpecimen baseSpecimen = FhirUtil.getBaseSpecimen(study, specimen.getIdentifierFirstRep().getValue());
			                 
			                 if(baseSpecimen == null) {
			                	 baseSpecimen = new BaseSpecimen();
				                 accession.getBaseSpecimen().add(baseSpecimen);
				                 
				                 baseSpecimen.setID(specimen.getIdentifierFirstRep().getValue());
				                 
				                 SpecimenCollection specimenCollection = new SpecimenCollection();
				                 if(specimen.getCollection() != null && specimen.getCollection().getCollected() instanceof Period){
				                	 Period period = (Period)specimen.getCollection().getCollected();
				                	 specimenCollection.setCollectionEndDateTime(period.getEnd());
				                	 specimenCollection.setActualCollectionDateTime(period.getStart());
			
			                		 String plannedCollectionTimeElapsed = null, targetDesc = null, relationshipDesc = null;
				                	 List<Extension> specimenExts = specimen.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/cqf-relativeDateTime");
				                	 if(specimenExts != null && specimenExts.size() > 0) {
				                		 for(Extension extension : specimenExts.get(0).getExtension()) {
				                			 if("offset".equalsIgnoreCase(extension.getUrl())){
				                				 Duration duration = (Duration)extension.getValue();
				                				 plannedCollectionTimeElapsed = duration.getValue() + " " + duration.getCode();
				                			 }else if("relationship".equalsIgnoreCase(extension.getUrl())) {
				                				 relationshipDesc = extension.getValue().toString();
				                			 }else if("target".equalsIgnoreCase(extension.getUrl())) {
				                				 Reference reference = (Reference)extension.getValue();
				                				 targetDesc = reference.getReference();
				                			 }
				                		 }
				                	 }
				                	 //TO DO. Does not match xsd restrictions
				                	 // cvc-pattern-valid: Value '60 min' is not facet-valid with respect to pattern '\d\d\d-([01]\d|2[0123])-[012345]\d' for type '#AnonType_PlannedCollectionTimeElapsedSpecimenCollection'.
				                	 //specimenCollection.setPlannedCollectionTimeElapsed(plannedCollectionTimeElapsed);
				                	 specimenCollection.setPlannedCollectionTimeElapsedDescription(relationshipDesc + " " + targetDesc);
				                	 baseSpecimen.setSpecimenCollection(specimenCollection);
				                 }
				                 
				                 if(specimen.getReceivedTime() != null){
				                	 SpecimenTransport specimenTransport = new SpecimenTransport();
				                	 specimenTransport.setReceivedDateTime(specimen.getReceivedTime());
				                	 
				                	 baseSpecimen.setSpecimenTransport(specimenTransport);
				                 }
				                 
				                 //TO DO
				                 if(specimen.getNoteFirstRep() != null && specimen.getNoteFirstRep().getAuthor() != null){
				                	String comment = null;
				 					for(Identifier identifier : practitioner.getIdentifier()) {
										if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0
											&& identifier.getAssigner().getReference().equals(organization.getIdentifierFirstRep().getAssigner().getReference())) {
											comment = identifier.getValue();
											break;
										}
									}
				 					
				 					if(comment != null) {
					                	 SpecimenComment specimenComment = new SpecimenComment();
						                 specimenComment.setSource("I");
						                 specimenComment.setText(specimen.getNoteFirstRep().getText());
						                 baseSpecimen.getSpecimenComment().add(specimenComment);
				 					}
				                	 
									 for(Identifier identifier : organization.getIdentifier()) {
										 if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0) {
						                	 SpecimenComment specimenComment = new SpecimenComment();
							                 specimenComment.setSource("I");
							                 specimenComment.setText(specimen.getNoteFirstRep().getText());
							                 baseSpecimen.getSpecimenComment().add(specimenComment);

							                 break;
										 }
									 }

				                 }
				                 
				                 if(specimen.getType() != null && specimen.getType().getCoding() != null && 
				                		 specimen.getType().getCoding().size() > 0){
				                	 SpecimenMaterial specimenMaterial = new SpecimenMaterial();
				                	 specimenMaterial.setID(specimen.getType().getCodingFirstRep().getCode());
				                	 specimenMaterial.setName(specimen.getType().getCodingFirstRep().getDisplay());
				                	 specimenMaterial.setCodeListID(specimen.getType().getCodingFirstRep().getSystem());
				                	 
				                	 baseSpecimen.setSpecimenMaterial(specimenMaterial);
				                 }
				                 
				                 if(patient.getBirthDate() != null) {
				                	 SubjectAtCollection subjectAtCollection = new SubjectAtCollection();
				                	 Calendar calendar = new GregorianCalendar();
				                	 calendar.setTime(patient.getBirthDate());
				                	 LocalDate bday = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)); 
				                	 LocalDate today = LocalDate.now(); 
				                	 java.time.Period age = java.time.Period.between(bday, today); 
			
				                	 Double patientAge = new Double(age.getYears());
				                	 subjectAtCollection.setAgeAtCollection(patientAge);
				                	 subjectAtCollection.setAgeUnits("Y");
				                	 //TO DO
				                	 //subjectAtCollection.setFastingStatus(specimen.getCollection());
				                	 baseSpecimen.setSubjectAtCollection(subjectAtCollection);
				                 }	
			                 }
	
		            	 }
		                 if(diagnosisReport.getIdentifierFirstRep() != null && diagnosisReport.getIdentifierFirstRep().getUse().compareTo(IdentifierUse.OFFICIAL) == 0 
		                		 && diagnosisReport.getIdentifierFirstRep().getValue() != null){
			                 BaseSpecimen baseSpecimen = FhirUtil.getBaseSpecimen(study, specimen.getIdentifierFirstRep().getValue());
			                 
		                	 BaseBattery baseBattery = FhirUtil.getBaseBattery(study, diagnosisReport.getIdentifierFirstRep().getValue());

		                	 if(baseBattery == null) {
		                		 baseBattery = new BaseBattery();
			                	 baseSpecimen.getBaseBattery().add(baseBattery);
			                	 baseBattery.setID(diagnosisReport.getIdentifierFirstRep().getValue());
			                	 //TO DO ..Max of 40 characters
			                	 baseBattery.setName(diagnosisReport.getCode().getCodingFirstRep().getDisplay().substring(0, 40));
		                	 }
		                	 
		                	 BaseTest baseTest = new BaseTest();
		                	 baseBattery.getBaseTest().add(baseTest);
		                	 
		                	 //Map Values
		                	 if(serviceRequest.hasDoNotPerform()) {
		                		baseTest.setStatus("N");
		                	 }else {
		                		 	if("COMPLETED".equals(serviceRequest.getStatus().getDisplay())){
				                		baseTest.setStatus("D");
		                		 	}else {
				                		baseTest.setStatus("X");
		                		 	}
		                	 }
		                	 
		                	 if(observation.getEffective() != null && observation.getEffectiveDateTimeType() instanceof DateTimeType){
		                		 baseTest.setTestingDateTime(observation.getEffectiveDateTimeType().getValue());	 
		                	 }
		                	 
		 		            List<Extension> studyTestTypeExtns = observation.getExtensionsByUrl("https://fhirtest.uhn.ca/baseDstu3/StructureDefinition/StudyTestType");
	
		                	 if(studyTestTypeExtns != null && studyTestTypeExtns.size() > 0){
		 		            	CodeableConcept codeableConcept = (CodeableConcept) studyTestTypeExtns.get(0).getValue();
		                		String code = codeableConcept.getCodingFirstRep().getCode();
		                		baseTest.setTestType(code);
		                	 }
		                	 
		                	
							 for(Identifier identifier : organization.getIdentifier()) {
								 if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0) {
				                	 PerformingLab performingLab = new PerformingLab();
				                	 performingLab.setID(identifier.getValue());
				                	 performingLab.setName(organization.getName());
				                	 
				                	 baseTest.setPerformingLab(performingLab);
									 break;
								 }
							}

		                	 
		 					for(Identifier identifier : observation.getIdentifier()) {
								if(IdentifierUse.OFFICIAL.compareTo(identifier.getUse()) == 0) {
				                	 LabTest labTest = new LabTest();
				                	 labTest.setID(observation.getIdentifierFirstRep().getValue());                	 
				                	 labTest.setName(observation.getCode().getCodingFirstRep().getDisplay());
				                	 labTest.setAdditionalDescription(observation.getComment());
				                	 baseTest.setLabTest(labTest);
								}
							}		                 	 
		
							for(Identifier identifier : observation.getIdentifier()) {
								if(IdentifierUse.SECONDARY.compareTo(identifier.getUse()) == 0) {
			                		 ReceiverTest receiverTest = new ReceiverTest();
			                		 receiverTest.setID(identifier.getValue());
			                		 receiverTest.setName(observation.getCode().getCodingFirstRep().getDisplay());
			                		 
			                		 baseTest.setReceiverTest(receiverTest);   
			                		 break;
								}
							}
			
		                	 if(observation.getCode().getCodingFirstRep() != null && observation.getCode().getCodingFirstRep().getSystem() != null
		                			&& observation.getCode().getCodingFirstRep().getSystem().contains("loinc") ){
		                		 LOINCTestCode loincTestCode = new LOINCTestCode();
		                		 loincTestCode.setValue(observation.getCode().getCodingFirstRep().getCode());
		                		 loincTestCode.setCodeListID(observation.getCode().getCodingFirstRep().getSystem());
		                		 
		                		 baseTest.setLOINCTestCode(loincTestCode);
		                	 }
		                	 
			 		         List<Extension> testLevelCommentExtns = observation.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/event-note");
			 		       	
			 		         for(Extension extn : testLevelCommentExtns) {
			 		        	 if(extn.getValue() instanceof Annotation) {
			 		        		 Annotation annotation = (Annotation) extn.getValue();
			 			            JAXBElement<String> testLevelCommentQName = factory.createTestLevelComment(subjectId);
			 			             testLevelCommentQName.setValue(annotation.getText());
			 		        		 baseTest.getTestLevelComment().add(annotation.getText());
			 		        		 break;
			 		        	 }

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
		                	 
		                	 //TO DO Value 'H' is not facet-valid with respect to enumeration '[LP, LT, LN, N, HN, HT, HP, AB]'. It must be a value from the enumeration.
	            			 //baseResult.setAlertFlag(observation.getInterpretation().getCodingFirstRep().getCode());  
	            			 baseResult.setAlertFlag("HN"); 
	            			 //TO DO cvc-enumeration-valid: Value 'H' is not facet-valid with respect to enumeration '[D+, D-]'. It must be a value from the enumeration.
	            			 //baseResult.setDeltaFlag(observation.getInterpretation().getCodingFirstRep().getCode());   
	            			 baseResult.setDeltaFlag("D+");
	            			 //TO DO ExclusionFlag="http://hl7.org/fhir/v2/0078" BlindingFlag="DEMO"
	            			 //baseResult.setExclusionFlag(observation.getInterpretation().getCodingFirstRep().getSystem()); 
	            			 baseResult.setExclusionFlag("LX");
	            			 //TO DO 
	            			 //baseResult.setBlindingFlag(observation.getMeta().getSecurityFirstRep().getCode()); 
	            			 baseResult.setBlindingFlag("S");
	
	            			 ToxicityGrade toxicityGrade = new ToxicityGrade();
	            			 toxicityGrade.setValue(observation.getInterpretation().getCodingFirstRep().getCode());                			 
	            			 toxicityGrade.setCodeListID(observation.getInterpretation().getCodingFirstRep().getSystem());
	            			 
	            			 baseResult.setToxicityGrade(toxicityGrade);            			 
	
	
	
		                		 SingleResult singleResult = new SingleResult();
		                		 baseResult.getSingleResult().add(singleResult);
		                		 
				 		         List<Extension> resultClassExtns = observation.getExtensionsByUrl("http://fhirtest.uhn.ca/baseDstu3/StructureDefinition/result-Class");
					 		       	
				                 if(resultClassExtns != null && resultClassExtns.size() > 0){
				 		            CodeableConcept codeableConcept = (CodeableConcept) resultClassExtns.get(0).getValue();

		                			singleResult.setResultClass(codeableConcept.getCoding().get(0).getCode());            			 
				                 }		                		 
		                		 
		                		 if(observation.getValue() != null && !(observation.getValueQuantity() instanceof Quantity)){
		                			 TextResult textResult = new TextResult();
		                			 textResult.setValue(observation.getValueCodeableConcept().getText());
		                			 textResult.setCodeListID(observation.getValueCodeableConcept().getCodingFirstRep().getSystem());
		                			 singleResult.setTextResult(textResult);
		                		 }		 
		                		 
		                		 if(observation.getValue() != null && (observation.getValue() instanceof Quantity)){
		                			 NumericResult numericResult = new NumericResult();
		                			 numericResult.setValue(observation.getValueQuantity().getValue().doubleValue());
		                			 //TO DO
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
		                		 
		                		 //TO DO Not sure about 'C'
		                		 if(singleResult.getTextResult() != null && singleResult.getTextResult().getValue().contains(">")) {
		                			 singleResult.setResultType("G");
		                		 }else if(singleResult.getTextResult() != null && singleResult.getTextResult().getValue().contains("<")) {
		                			 singleResult.setResultType("L");
		                		 }else if(singleResult.getTextResult() != null) {
		                			 singleResult.setResultType("T");
		                		 }else if(singleResult.getNumericResult() != null) {
		                			 singleResult.setResultType("N");
		                		 }else {
		                			 singleResult.setResultType("R");
		                		 }
		                	 
		                	 if(observation.getIssued() != null){
		                			 baseResult.setReportedDateTime(observation.getIssued());
		                	 }
		                	                	 
		                 }
		                 
		              }
		             } 
			}
			String xml =  FhirUtil.convertToXml(gtp);
			String result = FhirUtil.validateXml(xml, schemaUtil.getSchema());
			
			System.out.println("\n*********************************************************\n");

			if(!StringUtils.isAllEmpty(result)) {
				System.out.println(result);
			}
			
			System.out.println("\n*********************************************************\n");
			System.out.println(xml);
			
						
	}catch(Exception e){
		System.out.println(e.getMessage());
		e.printStackTrace();
	}

	}


	private static String getInitials(String nameAsSingleString) {
		
		String initials = "";
		String[] names = nameAsSingleString.split(" ");
		
		for(String name : names) {
			initials = initials.concat(name.substring(0, 1));
		}
		
		return initials;
	}


	public static IQuery<IBaseBundle> getResearchSubjectQuery(FhirContext ctx, IGenericClient client, OptionReader optionReader){
    	
		ca.uhn.fhir.rest.gclient.IQuery<IBaseBundle> query = client.search().forResource(ResearchSubject.class);
		
		if(StringUtils.isNotBlank(optionReader.identifier)){
			query.and(new TokenClientParam("_id").exactly().code(optionReader.identifier)); 
		}
				
		return query;
		
    }

	/** Returns {@code obj.toString()}, or {@code ""} if {@code obj} is {@code null}. */
    private static String safeToString(@Nullable Object obj) {
      return obj == null ? "" : obj.toString();
    }


}
