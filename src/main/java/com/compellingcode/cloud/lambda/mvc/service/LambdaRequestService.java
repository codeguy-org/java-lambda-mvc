package com.compellingcode.cloud.lambda.mvc.service;

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
import com.compellingcode.cloud.lambda.mvc.enums.ContentType;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointVariableMismatchException;
import com.compellingcode.cloud.lambda.mvc.exception.NoMatchingEndpointException;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RequestDecoder;
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
		String ct = getContentType(request.getHeaders());
		ContentType contentType = ContentType.resolveType(ct);
		RequestDecoderFactory rdf = new RequestDecoderFactory();
		RequestDecoder rd = rdf.getRequestDecoder(contentType);
		rd.decode(body , request);

		return request;
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
	
	
	public LambdaResponse processRequest(EndpointTreeNode rootNode, LambdaRequest request, Context context) throws Exception {
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
		
		return (LambdaResponse)rp.getCallback().call(context,  request);
	}
}
