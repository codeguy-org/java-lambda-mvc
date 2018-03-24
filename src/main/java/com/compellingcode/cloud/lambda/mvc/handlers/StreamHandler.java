package com.compellingcode.cloud.lambda.mvc.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.services.LambdaRequestService;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;
import com.amazonaws.services.lambda.runtime.Context;

import org.apache.commons.io.input.BoundedReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public abstract class StreamHandler implements RequestStreamHandler {
    // Initialize the Log4j logger.
    static final Logger logger = LogManager.getLogger(StreamHandler.class);
    
    private LambdaRequestService lambdaRequestService = new LambdaRequestService();
    
    public StreamHandler() {
    	configure();
    }

	protected abstract void configure();
    
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		JSONObject data = acceptStreamConnection(inputStream);
		LambdaResponse response = processRequest(data, context);
		renderStream(outputStream, response);
	}

    protected JSONObject acceptStreamConnection(InputStream inputStream) throws IOException {
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new BoundedReader(new InputStreamReader(inputStream, "UTF-8"), 10 * 1024 * 1024));
		
		char[] buffer = new char[1 * 1024 * 1024];
		int size;
		while((size = br.read(buffer)) > -1) {
			out.append(buffer, 0, size);
		}
		
		br.close();
				
		return new JSONObject(out.toString());
    }
    
    protected void renderStream(OutputStream outputStream, LambdaResponse response) throws IOException {
		JSONObject output = new JSONObject();
		JSONObject headers = response.getHeaders();
		
		headers.put("Content-Type", response.getMimeType().getType());
		headers.put("Content-Length", response.getSize());
		
		output.put("headers", headers);
		output.put("body", response.getBody());
		output.put("statusCode", response.getStatusCode());
		
		outputStream.write(output.toString().getBytes());
    }
    
    protected LambdaResponse processRequest(JSONObject data, Context context) {
		LambdaRequest request = lambdaRequestService.getLambdaRequest(data);
		
		LambdaResponse response = lambdaRequestService.processRequest(request, context);
		
		return response;
    }
    
}
