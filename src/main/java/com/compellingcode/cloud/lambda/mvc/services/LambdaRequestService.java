package com.compellingcode.cloud.lambda.mvc.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointTreeNode;
import com.compellingcode.cloud.lambda.mvc.exceptions.EndpointVariableMismatchException;
import com.compellingcode.cloud.lambda.mvc.exceptions.NoMatchingEndpointException;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public class LambdaRequestService {
	
	static final Logger logger = LogManager.getLogger(LambdaRequestService.class);
	
	public LambdaRequest getLambdaRequest(JSONObject data) {
		LambdaRequest request = new LambdaRequest();
		
		request.setHeaders(getJSONObject(data, "headers"));
		request.setRequestContext(getJSONObject(data, "requestContext"));
		request.setIdentity(getJSONObject(request.getRequestContext(), "identity"));
		request.setPathParameters(getJSONObject(data, "pathParameters"));
		request.setQueryStringParameters(getJSONObject(data, "queryStringParameters"));
		request.setStageVariables(getJSONObject(data, "stageVariables"));
		
		request.setPath(data.getString("path"));
		request.setMethod(data.getString("httpMethod"));
		request.setIp(request.getIdentity().getString("sourceIp"));
		
		byte[] body = getBody(data);
		//todo: process body
		
		return request;
	}
	
	private byte[] getBody(JSONObject data) {
		byte[] body;
		String stringBody = "";
		
		boolean isBase64Encoded = false;
		
		if(data.has("isBase64Encoded")) {
			isBase64Encoded = data.getBoolean("isBase64Encoded");
		}
		
		if(data.has("body")) {
			String b = data.getString("body");
			if(b != null) {
				stringBody = b;
			}
		}
		
		if(stringBody.length() > 0) {
			if(isBase64Encoded) {
				body = Base64.getDecoder().decode(stringBody);
			} else {
				body = stringBody.getBytes();
			}
		} else {
			body = new byte[0];
		}
		
		return body;
	}
	
	private JSONObject getJSONObject(JSONObject data, String key) {
		JSONObject obj = new JSONObject();
		
		if(data.has(key) && !data.isNull(key)) {
			JSONObject o = data.getJSONObject(key);
			if(o != null) {
				obj = o;
			}
		}
		
		return obj;
	}
	
	
	public LambdaResponse processRequest(EndpointTreeNode rootNode, LambdaRequest request, Context context) throws NoMatchingEndpointException, EndpointVariableMismatchException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String proxy = "/";
		
		if(request.getPathParameters().has("proxy")) {
			String p = request.getPathParameters().getString("proxy");
			if(p != null) {
				if(p.length() > 0) {
					proxy = p;
				}
			}
		}
		
		RequestProcessor rp = rootNode.search(proxy);
		
		JSONObject pathParameters = request.getPathParameters();
		for(Entry<String, String> entry : rp.getVariables().entrySet()) {
			pathParameters.put(entry.getKey(), entry.getValue());
		}
		
		return (LambdaResponse)rp.getCallback().getMethod().invoke(rp.getCallback().getObject());
	}
}
