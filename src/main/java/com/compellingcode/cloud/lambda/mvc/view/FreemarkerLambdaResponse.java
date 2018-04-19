package com.compellingcode.cloud.lambda.mvc.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.compellingcode.cloud.lambda.mvc.enums.MimeType;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerLambdaResponse extends LambdaResponse {
	static final Logger logger = LogManager.getLogger(FreemarkerLambdaResponse.class);
			
	private static Configuration freemarkerConfiguration = null;
	
	private String template;
	
	private static Configuration getFreemarkerConfiguration() {
		if(freemarkerConfiguration == null) {
			freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
			freemarkerConfiguration.setDefaultEncoding("UTF-8");
			freemarkerConfiguration.setClassForTemplateLoading(FreemarkerLambdaResponse.class, "/templates");
		}
		
		return freemarkerConfiguration;
	}
	
	public FreemarkerLambdaResponse(String template) {
		this.template = template;
		this.setMimeType(MimeType.HTML);
	}

	@Override
	public String getBody() throws LambdaResponseException {
		try {
			Template tpl = getFreemarkerConfiguration().getTemplate(template);
			StringWriter sw = new StringWriter();
			tpl.process(getVariables(), sw);
			return sw.toString();
		} catch(Exception ex) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String stackTrace = sw.toString();
			logger.debug(stackTrace);
			throw new LambdaResponseException(ex);
		}
	}

}
