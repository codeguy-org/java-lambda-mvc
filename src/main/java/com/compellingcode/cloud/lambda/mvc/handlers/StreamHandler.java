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
		
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new BoundedReader(new InputStreamReader(inputStream, "UTF-8"), 10 * 1024 * 1024));
		
		char[] buffer = new char[1 * 1024 * 1024];
		int size;
		while((size = br.read(buffer)) > -1) {
			out.append(buffer, 0, size);
		}
		
		br.close();
				
		JSONObject data = new JSONObject(out.toString());
		logger.debug(data.toString());
		
		LambdaRequest request = lambdaRequestService.getLambdaRequest(data);
		data = null;
		
		LambdaResponse response = lambdaRequestService.processRequest(request, outputStream, context);

		JSONObject output = new JSONObject();
		JSONObject headers = response.getHeaders();
		
		headers.put("Content-Type", response.getMimeType().getType());
		headers.put("Content-Length", response.getSize());
		
		output.put("headers", headers);
		output.put("body", response.getBody());
		output.put("statusCode", response.getStatusCode());
		
		outputStream.write(output.toString().getBytes());
	}

    
}
