package com.compellingcode.cloud.lambda.mvc.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointTreeNode;
import com.compellingcode.cloud.lambda.mvc.exception.EndProcessingException;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointConflictException;
import com.compellingcode.cloud.lambda.mvc.exception.ExitFilterChainException;
import com.compellingcode.cloud.lambda.mvc.exception.FilterException;
import com.compellingcode.cloud.lambda.mvc.exception.InvalidContentTypeException;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;
import com.compellingcode.cloud.lambda.mvc.exception.NoMatchingEndpointException;
import com.compellingcode.cloud.lambda.mvc.exception.ScheduledEventException;
import com.compellingcode.cloud.lambda.mvc.filter.RequestFilter;
import com.compellingcode.cloud.lambda.mvc.filter.ResponseFilter;
import com.compellingcode.cloud.lambda.mvc.service.LambdaControllerService;
import com.compellingcode.cloud.lambda.mvc.service.LambdaRequestService;
import com.compellingcode.cloud.lambda.mvc.view.DefaultErrorResponse;
import com.compellingcode.cloud.lambda.mvc.view.FreemarkerLambdaResponse;
import com.compellingcode.cloud.lambda.mvc.view.JSONLambdaResponse;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;
import com.compellingcode.cloud.lambda.mvc.view.ThymeleafLambdaResponse;
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
    
    private List<RequestFilter> requestFilters = new ArrayList<RequestFilter>();
    private List<ResponseFilter> responseFilters = new ArrayList<ResponseFilter>();
    
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
		} catch(ScheduledEventException ex) {
			warmup(outputStream);
		} catch(InvalidContentTypeException ex) {
			logger.fatal(ex.getMessage());
			try {
				renderStream(outputStream, errorHandler(501));
			} catch (LambdaResponseException e) {
				logger.fatal(getStackTrace(ex));
			}
		} catch(NoMatchingEndpointException ex) {
			try {
				renderStream(outputStream, errorHandler(404));
			} catch(LambdaResponseException e) {
				logger.fatal(getStackTrace(e));
			}
		} catch(EndProcessingException ex) {
			try {
				renderStream(outputStream, ex.getResponse());
			} catch (LambdaResponseException e) {
				logger.fatal(getStackTrace(e));
			}
		} catch(Exception ex) {
			logger.fatal(getStackTrace(ex));
			try {
				renderStream(outputStream, errorHandler(500));
			} catch (LambdaResponseException e) {
				logger.fatal(getStackTrace(ex));
			}
		}
	}
	
	protected void warmup(OutputStream outputStream) throws IOException {
		// warm up freemarker
		try {
			FreemarkerLambdaResponse freemarker = new FreemarkerLambdaResponse("default/empty");
			freemarker.getBody();
		} catch (LambdaResponseException e) {
			// ok
		}
		
		try {
			ThymeleafLambdaResponse thymeleaf = new ThymeleafLambdaResponse("default/empty");
			thymeleaf.getBody();
		} catch(Exception ex) {
			// ok
		}

		// warm up jackson
		try {
			renderStream(outputStream, new JSONLambdaResponse("pong"));
		} catch (LambdaResponseException e) {
			logger.fatal(getStackTrace(e));
		} catch (IOException e) {
			logger.fatal(e);
		}
	}
	
	protected LambdaResponse errorHandler(int errorNumber) {
		return new DefaultErrorResponse(errorNumber);
	}
	
	public void addController(Object controller) throws Exception {
		lambdaControllerService.addController(rootNode, controller);
	}
	
	public void addMethod(String path, Object controller, String method, int requestMethod) throws Exception {
		lambdaControllerService.addMethod(rootNode, path, controller, method, requestMethod);
	}
	
	public void addMethod(String path, Object controller, Method method, int requestMethod) throws Exception {
		lambdaControllerService.addMethod(rootNode, path, controller, method, requestMethod);
	}
	
	public void addRequestFilter(RequestFilter filter) {
		requestFilters.add(filter);
	}
	
	public void addResponseFilter(ResponseFilter filter) {
		responseFilters.add(filter);
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
    
    protected void renderStream(OutputStream outputStream, LambdaResponse response) throws IOException, LambdaResponseException {
		JSONObject output = new JSONObject();
		JSONObject headers = response.getHeaders();
		
		headers.put("Content-Type", response.getMimeType().getType());
		headers.put("Content-Length", response.getSize());
		
		try {
			output.put("body", response.getBody());
		} catch(LambdaResponseException ex) {
			throw ex;
		}
		output.put("headers", headers);
		output.put("statusCode", response.getStatusCode());
		output.put("isBase64Encoded", response.isBase64Encoded());
		
		outputStream.write(output.toString().getBytes());
    }
    
    protected LambdaResponse processRequest(JSONObject data, Context context) throws Exception {
		LambdaRequest request = lambdaRequestService.getLambdaRequest(data);
		
		logger.info(String.format("Incoming request for %s from %s", request.getPath(), request.getIp()));
		
		RequestProcessor rp = lambdaRequestService.getProcessor(rootNode, request);
		
		applyRequestFilters(request, context, rp.getCallback().getMethod());
		
		JSONObject pathParameters = request.getPathParameters();
		for(Entry<String, String> entry : rp.getVariables().entrySet()) {
			pathParameters.put(entry.getKey(), entry.getValue());
		}
		
		LambdaResponse response = (LambdaResponse)rp.getCallback().call(context,  request);
		
		applyResponseFilters(request, response, context, rp.getCallback().getMethod());
		
		return response;
    }
    
    private void applyRequestFilters(LambdaRequest request, Context context, Method m) throws EndProcessingException, FilterException {
    	try {
    		for(RequestFilter filter : requestFilters) {
    			filter.processFilter(request, context, m);
    		}
    	} catch(ExitFilterChainException ex) {
    		// done processing
    	}
    }
    
    private LambdaResponse applyResponseFilters(LambdaRequest request, LambdaResponse response, Context context, Method m) throws EndProcessingException, FilterException {
    	try {
    		for(ResponseFilter filter : responseFilters) {
    			response = filter.processFilter(request, response, context, m);
    		}
    	} catch(ExitFilterChainException ex) {
    		// done processing
    	}
    	
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
