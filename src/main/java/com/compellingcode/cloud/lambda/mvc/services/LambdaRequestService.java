package com.compellingcode.cloud.lambda.mvc.services;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.view.HtmlLambdaResponse;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public class LambdaRequestService {
	
	static final Logger logger = LogManager.getLogger(LambdaRequestService.class);
	
	public LambdaRequest getLambdaRequest(JSONObject data) {
		LambdaRequest request = new LambdaRequest();
		
		request.setHeaders(getHeaders(data));
		
		// todo: process body
		
		return request;
	}
	
	private String getBody(JSONObject data) {
		String body = "";
		boolean isBase64Encoded = false;
		
		if(data.has("isBase64Encoded")) {
			isBase64Encoded = data.getBoolean("isBase64Encoded");
		}
		
		if(data.has("body")) {
			String b = data.getString("body");
			if(b != null) {
				body = b;
			}
		}
		
		if(isBase64Encoded && body.length() > 0) {
			body = new String(Base64.getDecoder().decode(body), Charset.forName("UTF-8"));
		}
		
		return body;
	}
	
	private JSONObject getHeaders(JSONObject data) {
		JSONObject headers = new JSONObject();
		
		if(data.has("headers")) {
			JSONObject h = data.getJSONObject("headers");
			if(h != null) {
				headers = h;
			}
		}
		
		return headers;
	}
	
	
	public LambdaResponse processRequest(LambdaRequest lambdaRequest, Context context) {
		return new HtmlLambdaResponse();
	}
}
