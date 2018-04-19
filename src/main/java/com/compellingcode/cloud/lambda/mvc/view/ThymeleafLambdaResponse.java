package com.compellingcode.cloud.lambda.mvc.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.compellingcode.cloud.lambda.mvc.enums.MimeType;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy;

public class ThymeleafLambdaResponse extends LambdaResponse {
	static final Logger logger = LogManager.getLogger(ThymeleafLambdaResponse.class);
	
	private static TemplateEngine templateEngine = null;
			
	private String template;
	private Locale locale;
	
	private static TemplateEngine getTemplateEngine() {
		if(templateEngine != null)
			return templateEngine;
		
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode("HTML5");
		resolver.setSuffix(".html");
		resolver.setPrefix("/templates/");
		
		templateEngine = new TemplateEngine();
		templateEngine.addDialect(new LayoutDialect(new GroupingStrategy()));
		templateEngine.setTemplateResolver(resolver);
		
		return templateEngine;
	}
	
	public ThymeleafLambdaResponse(String template) {
		this.template = template;
		this.locale = Locale.getDefault();
		this.setMimeType(MimeType.HTML);
	}

	public ThymeleafLambdaResponse(String template, Locale locale) {
		this.template = template;
		this.locale = locale;
		this.setMimeType(MimeType.HTML);
	}

	@Override
	public String getBody() throws LambdaResponseException {
		try {
			Context context = new Context(locale);
			context.setVariables(this.getVariables());
			return getTemplateEngine().process(template, context);
		} catch(Exception ex) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String stackTrace = sw.toString();
			logger.error(stackTrace);
			
			throw new LambdaResponseException(ex);
		}
	}

}
