package com.compellingcode.cloud.lambda.mvc.domain;

import java.io.Serializable;

import org.json.JSONObject;

public class LambdaResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONObject headers = new JSONObject();
	private byte[] body;
	private boolean base64Encode;
	
	public byte[] getBody() {
		return body;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public boolean isBase64Encode() {
		return base64Encode;
	}
	
	public void setBase64Encode(boolean base64Encode) {
		this.base64Encode = base64Encode;
	}
	
	public void setHeader(String header, String value) {
		headers.put(header,  value);
	}
	
	public String getHeader(String header) {
		return headers.getString(header);
	}
}
