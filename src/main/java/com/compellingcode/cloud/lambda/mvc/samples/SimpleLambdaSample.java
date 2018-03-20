package com.compellingcode.cloud.lambda.mvc.samples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.compellingcode.cloud.lambda.mvc.handlers.StreamHandler;

public class SimpleLambdaSample extends StreamHandler {
	
	static final Logger logger = LogManager.getLogger(SimpleLambdaSample.class);

	@Override
	protected void configure() {
		logger.debug("configure called");
	}

}
