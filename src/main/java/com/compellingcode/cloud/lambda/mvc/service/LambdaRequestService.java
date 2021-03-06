package com.compellingcode.cloud.lambda.mvc.service;

import java.util.Base64;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointTreeNode;
import com.compellingcode.cloud.lambda.mvc.endpoint.RequestMethod;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointVariableMismatchException;
import com.compellingcode.cloud.lambda.mvc.exception.InvalidContentTypeException;
import com.compellingcode.cloud.lambda.mvc.exception.InvalidEndpointPathException;
import com.compellingcode.cloud.lambda.mvc.exception.NoMatchingEndpointException;
import com.compellingcode.cloud.lambda.mvc.exception.RequestDecoderException;
import com.compellingcode.cloud.lambda.mvc.exception.ScheduledEventException;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RawRequestDecoder;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RequestDecoder;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;
import com.compellingcode.utils.parser.form.multipart.domain.FormElement;

public class LambdaRequestService {
	
	static final Logger logger = LogManager.getLogger(LambdaRequestService.class);
	
	public LambdaRequest getLambdaRequest(JSONObject data) throws InvalidContentTypeException, RequestDecoderException, ScheduledEventException {
		LambdaRequest request = new LambdaRequest();

		try {
			String s = data.getString("detail-type");
			if("Scheduled Event".equals(s)) {
				throw new ScheduledEventException();
			}
		} catch(ScheduledEventException ex) {
			throw ex;
		} catch(Exception ex) {
			// not found, continue as usual, nothing to do here
		}
		
		request.setHeaders(getJSONObject(data, "headers"));
		request.setRequestContext(getJSONObject(data, "requestContext"));
		request.setIdentity(getJSONObject(request.getRequestContext(), "identity"));
		request.setPathParameters(getJSONObject(data, "pathParameters"));
		request.setStageVariables(getJSONObject(data, "stageVariables"));
		
		request.setQueryStringParameters(getJSONObject(data, "queryStringParameters"));
		request.setPostParameters(new JSONObject());
		
		configureRequestParameters(request);
		
		request.setPath(data.getString("path"));
		request.setMethod(data.getString("httpMethod"));
		request.setIp(request.getIdentity().getString("sourceIp"));
		
		byte[] body = getBody(data);
		String ct = getContentType(request.getHeaders());
		RequestDecoder rd;
		if(ct != null) {
			rd = new RequestDecoderFactory().getRequestDecoder(ct);
		} else {
			rd = new RawRequestDecoder();
		}
		rd.decode(body, request);

		return request;
	}
	
	private void configureRequestParameters(LambdaRequest request) {
		JSONObject requestParameters = new JSONObject();
		JSONObject queryStringParameters = request.getQueryStringParameters();
		JSONObject postParameters = request.getPostParameters();
		
		for(String key : queryStringParameters.keySet()) {
			if(queryStringParameters.isNull(key))
				requestParameters.put(key, "");
			
			requestParameters.put(key, new FormElement(key, queryStringParameters.getString(key)));
		}
		
		for(String key : postParameters.keySet()) {
			requestParameters.put(key, postParameters.get(key));
		}
		
		request.setRequestParameters(requestParameters);
	}
	
	private String getContentType(JSONObject data) {
		String contentType = null;
		
		if(data.has("content-type") && !data.isNull("content-type")) {
			contentType = data.getString("content-type").toLowerCase();
		}
		
		if(data.has("Content-Type") && !data.isNull("Content-Type")) {
			contentType = data.getString("content-type").toLowerCase();
		}
		
		if(data.has("Content-type") && !data.isNull("Content-type")) {
			contentType = data.getString("content-type").toLowerCase();
		}
		
		if(data.has("CONTENT-TYPE") && !data.isNull("CONTENT-TYPE")) {
			contentType = data.getString("content-type").toLowerCase();
		}
		
		return contentType;
	}
	
	private byte[] getBody(JSONObject data) {
		byte[] body;
		String stringBody = "";
		
		boolean isBase64Encoded = false;
		
		if(data.has("isBase64Encoded")) {
			isBase64Encoded = data.getBoolean("isBase64Encoded");
		}
		
		if(data.has("body") && !data.isNull("body")) {
			stringBody = data.getString("body");
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
			obj = data.getJSONObject(key);
		}
		
		return obj;
	}
	
	public RequestProcessor getProcessor(EndpointTreeNode rootNode, LambdaRequest request) throws NoMatchingEndpointException, EndpointVariableMismatchException, InvalidEndpointPathException {
		String proxy = "/";
		
		if(request.getPathParameters().has("proxy")) {
			String p = request.getPathParameters().getString("proxy");
			if(p != null) {
				if(p.length() > 0) {
					proxy = p;
				}
			}
		}
		
		return rootNode.search(proxy, RequestMethod.getMethod(request.getMethod()));
	}
	
}
