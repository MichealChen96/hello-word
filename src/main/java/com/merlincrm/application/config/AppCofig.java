package com.merlincrm.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppCofig {
	
	@Value("${file.repository.path}")
	private String filePath;
	
	@Value("${sso.service}")
	private String ssoService;
	
	@Value("${sso.service.teacher}")
	private String ssoServiceTeacher;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSsoService() {
		return ssoService;
	}

	public void setSsoService(String ssoService) {
		this.ssoService = ssoService;
	}

	public String getSsoServiceTeacher() {
		return ssoServiceTeacher;
	}

	public void setSsoServiceTeacher(String ssoServiceTeacher) {
		this.ssoServiceTeacher = ssoServiceTeacher;
	}
	
}
