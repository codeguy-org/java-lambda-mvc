package com.compellingcode.cloud.lambda.mvc.domain;

import java.io.Serializable;

import org.json.JSONObject;

public class LambdaRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONObject headers;
	private JSONObject identity;
	
	public JSONObject getHeaders() {
		return headers;
	}
	
	public void setHeaders(JSONObject headers) {
		this.headers = headers;
	}
	
	public JSONObject getIdentity() {
		return identity;
	}
	
	public void setIdentity(JSONObject identity) {
		this.identity = identity;
	}
	
}
