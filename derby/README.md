Apache Derby files for samples of FHIR entities 

Hapi-fhir-jpaserver uses Apache Derby database for persisting information about FHIR entities. The database files are primarily located under the folders named jpaserver_derby_files and lucenefiles.

Instructions for copying persistent information (Derby files)
1) Stop tomcat 
2) If hapi-fhir-jpaserver application has already been installed, delete the folder hapi-fhir-jpaserver-example under tomcat/webapps. Keep the war file as it is. If hapi-fhir-jpaserver has never been installed, please copy the hapi-fhir-jpaserver war to the webapps folder.
3) Delete the folders (jpaserver_derby_files and lucenefiles) under tomcat/target if the folders exist
4) Delete the folders (jpaserver_derby_files and lucenefiles) under tomcat/bin/target if the folders exist
5) Copy the supplied new folders jpaserver_derby_files and lucenefiles to tomcat/bin/target
6) Start tomcat by clicking on startup.bat in tomcat/bin
