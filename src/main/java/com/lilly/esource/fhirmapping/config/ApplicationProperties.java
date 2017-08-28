package com.lilly.esource.fhirmapping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private String fHIRServerURL = "";

	public String getfHIRServerURL() {
		return fHIRServerURL;
	}

	public void setfHIRServerURL(String fHIRServerURL) {
		this.fHIRServerURL = fHIRServerURL;
	}
}
