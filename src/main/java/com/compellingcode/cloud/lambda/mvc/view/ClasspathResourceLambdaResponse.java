package com.compellingcode.cloud.lambda.mvc.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.compellingcode.cloud.lambda.mvc.enums.MimeType;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

public class ClasspathResourceLambdaResponse extends LambdaResponse {
	static final Logger logger = LogManager.getLogger(ClasspathResourceLambdaResponse.class);
	
	private String filename;

	public ClasspathResourceLambdaResponse(String filename, boolean inline) {
		if(filename.length() > 0 && filename.charAt(0) == '/')
			filename = filename.substring(0);

		setFilename(filename, inline);
		setBase64Encoded(true);
	}
	
	public void setFilename(String filename, boolean inline) {
		this.filename = filename;
		
		String disposition;
		if(inline)
			disposition = "inline";
		else
			disposition = "attachment";
		
		getHeaders().put("Content-Disposition", disposition + "; filename=\"" + getBasename(filename) + "\"");
		setMimeType(MimeType.findType(getExtension(filename)));
	}
	
	private String getBasename(String filename) {
		String basename = "";
		
		String parts[] = filename.split("/");
		if(parts.length > 0) {
			basename = parts[parts.length - 1];
		}
		
		return basename;
	}
	
	private String getExtension(String filename) {
		String extension = null;
		
		String parts[] = filename.split("\\.");
		if(parts.length > 0) {
			extension = parts[parts.length - 1];
		}
		
		return extension;
	}

	@Override
	public String getBody() throws LambdaResponseException {
		try {
			String f = "static/" + filename;
			byte[] file = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(f));
			setSize(file.length);
			return new String(Base64.getEncoder().encode(file));
		} catch(Exception ex) {
			throw new LambdaResponseException(ex);
		}
	}

}
