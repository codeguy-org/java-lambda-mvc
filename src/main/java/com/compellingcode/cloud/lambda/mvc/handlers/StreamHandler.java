package com.compellingcode.cloud.lambda.mvc.handlers;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public abstract class StreamHandler implements RequestStreamHandler {
    // Initialize the Log4j logger.
    static final Logger logger = LogManager.getLogger(StreamHandler.class);
    
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
				
		logger.debug(out.toString());
		
		JSONObject output = new JSONObject();
		JSONObject headers = new JSONObject();
		output.put("headers", headers);

		output.put("body", "done");
		
		headers.put("Content-Type", "text/html");
		
		outputStream.write(output.toString().getBytes());
		
	}
    
}
