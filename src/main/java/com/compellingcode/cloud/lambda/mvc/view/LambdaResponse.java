package com.compellingcode.cloud.lambda.mvc.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.compellingcode.cloud.lambda.mvc.enums.MimeType;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

public abstract class LambdaResponse {
	
	private JSONObject headers = new JSONObject();
	private Map<String, Object> variables = new HashMap<String, Object>();
	protected String statusCode = "200";
	protected boolean base64Encoded = false;
	protected int size = 0;
	protected MimeType mimeType = MimeType.HTML;
	
	public abstract String getBody() throws LambdaResponseException;

	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public String getHeader(String key, String defaultValue) {
		if(!headers.has(key))
			return defaultValue;
		
		Object value = headers.get(key);
		
		if(value == null)
			return defaultValue;
		
		if(!String.class.isAssignableFrom(value.getClass()))
			return defaultValue;
		
		return (String)value;
	}
	
	public JSONObject getHeaders() {
		return headers;
	}
	
	public void setVariable(String key, Object value) {
		variables.put(key, value);
	}

	public void setVariable(String key, boolean value) {
		variables.put(key, value);
	}

	public void setVariable(String key, int value) {
		variables.put(key, value);
	}

	public void setVariable(String key, long value) {
		variables.put(key, value);
	}

	public void setVariable(String key, float value) {
		variables.put(key, value);
	}

	public void setVariable(String key, double value) {
		variables.put(key, value);
	}

	public void setVariable(String key, Collection<?> value) {
		variables.put(key, value);
	}

	public void setVariable(String key, Map<?, ?> value) {
		variables.put(key, value);
	}
	
	protected Map<String, Object> getVariables() {
		return variables;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isBase64Encoded() {
		return base64Encoded;
	}

	public void setBase64Encoded(boolean base64Encoded) {
		this.base64Encoded = base64Encoded;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	
}
