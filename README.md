This JHipster project has material and samples to facilitate a mapping of FHIR xml to Lab Data Model (1.0.2) xml under a given set of assumptions.

The project can be run in multiple ways,

1) As a Spring Boot application with the Main class as FhirMappingApp 
2) Deployed to an application server
3) Run ./mvnw -Pdev package and then the generated war in the target folder can be run from the command line (java -jar fhir-mapping-0.0.1-SNAPSHOT.war)


Additionally, before deployment, the configuration url in application.yml needs to point to the desired FHIR Server.

Once deployed successfully, the application will be available at http://localhost:8088.

Login credentials are admin/admin.

After a successful login, the home page should be displayed which contains a slideshow of FHIR mappings to LDM. Additionally, there should be two menu items at the top, Target State and Current State.
Target State indicates the mapping of FHIR elements to LDM with a general set of assumptions. Current State indicates the mapping of FHIR elements to LDM with certain hardcoded assumptions. All the assumptions are 
indicated in the slides in home page.

Under Target State menu, two options are available, Observation and Patient. The Observation page facilitates a search for FHIR observations based on Observation Id and/or Date Range. The search results 
are obtained from the FHIR Server and then mapped to LDM. If the mapping was successful, then LDM Xml is displayed in a popup. Similarly, patient page facilitates a search for FHIR Observations 
based on given Patient search criteria. 

The Current State Observation search page results is similar as above except that there are more hardcoded assumptions.









