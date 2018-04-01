package com.compellingcode.cloud.lambda.mvc.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointTreeNode;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointConflictException;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;
import com.compellingcode.cloud.lambda.mvc.service.LambdaControllerService;
import com.compellingcode.cloud.lambda.mvc.service.LambdaRequestService;
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
    private LambdaControllerService lambdaControllerService = new LambdaControllerService();
    private EndpointTreeNode rootNode = new EndpointTreeNode();
    
    public StreamHandler() {
    	try {
    		configure();
    	} catch(Exception ex) {
    		try {
				logger.fatal(getStackTrace(ex));
			} catch (IOException e) {
				logger.fatal(ex);
			}
    	}
    }

	protected abstract void configure() throws EndpointConflictException;
	
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		try {
			JSONObject data = acceptStreamConnection(inputStream);
			LambdaResponse response = processRequest(data, context);
			renderStream(outputStream, response);
		} catch(Exception ex) {
			// todo: catch path not found exceptions, return different error types
			logger.fatal(getStackTrace(ex));
		}
	}
	
	public void addController(Object controller) throws Exception {
		lambdaControllerService.addController(rootNode, controller);
	}
	
	public void addMethod(String path, Object controller, String method) throws Exception {
		lambdaControllerService.addMethod(rootNode, path, controller, method);
	}
	
	public void addMethod(String path, Object controller, Method method) throws Exception {
		lambdaControllerService.addMethod(rootNode, path, controller, method);
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
		
		try {
			output.put("body", response.getBody());
		} catch(LambdaResponseException ex) {
			// todo: add error handler
		}
		output.put("headers", headers);
		output.put("statusCode", response.getStatusCode());
		output.put("isBase64Encoded", response.isBase64Encoded());
		
		outputStream.write(output.toString().getBytes());
    }
    
    protected LambdaResponse processRequest(JSONObject data, Context context) throws Exception {
		LambdaRequest request = lambdaRequestService.getLambdaRequest(data);
		
		LambdaResponse response = lambdaRequestService.processRequest(rootNode, request, context);
		
		return response;
    }

    private String getStackTrace(Throwable t) throws IOException {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	t.printStackTrace(pw);
    	
    	String output = sw.toString();
    	
    	pw.close();
    	sw.close();
    	
    	return output;
    }
}
