package com.compellingcode.cloud.lambda.mvc.domain;

import java.util.Map;

import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointCallback;

public class RequestProcessor {
	
	public EndpointCallback callback = null;
	public Map<String, Object> variables = null;

	public RequestProcessor(EndpointCallback callback, Map<String, Object> variables) {
		this.callback = callback;
		this.variables = variables;
	}

	public EndpointCallback getCallback() {
		return callback;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

}
