package com.compellingcode.cloud.lambda.mvc.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import com.compellingcode.cloud.lambda.mvc.enums.ContentType;

public class LambdaRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String path;
	private String method;
	private String ip;
	private byte[] body;
	private ContentType bodyType;
	private JSONObject headers;
	private JSONObject requestContext;
	private JSONObject identity;
	private JSONObject pathParameters;
	private JSONObject stageVariables;
	private JSONObject queryStringParameters;
	private JSONObject postParameters;
	private JSONObject requestParameters;
	
	public JSONObject getHeaders() {
		return headers;
	}
	
	public void setHeaders(JSONObject headers) {
		this.headers = headers;
	}
	
	public JSONObject getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(JSONObject requestContext) {
		this.requestContext = requestContext;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public JSONObject getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(JSONObject pathParameters) {
		this.pathParameters = pathParameters;
	}

	public JSONObject getStageVariables() {
		return stageVariables;
	}

	public void setStageVariables(JSONObject stageVariables) {
		this.stageVariables = stageVariables;
	}

	public JSONObject getQueryStringParameters() {
		return queryStringParameters;
	}

	public void setQueryStringParameters(JSONObject queryStringParameters) {
		this.queryStringParameters = queryStringParameters;
	}

	public JSONObject getPostParameters() {
		return postParameters;
	}

	public void setPostParameters(JSONObject postParameters) {
		this.postParameters = postParameters;
	}

	public JSONObject getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(JSONObject requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public JSONObject getIdentity() {
		return identity;
	}

	public void setIdentity(JSONObject identity) {
		this.identity = identity;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public String getBodyString() throws UnsupportedEncodingException {
		return new String(this.body, "UTF-8");
	}
	
	public void setBodyString(String body) throws UnsupportedEncodingException {
		if(body == null) {
			this.body = null;
		} else {
			this.body = body.getBytes("UTF-8");
		}
	}

	public ContentType getBodyType() {
		return bodyType;
	}

	public void setBodyType(ContentType bodyType) {
		this.bodyType = bodyType;
	}
	
}
