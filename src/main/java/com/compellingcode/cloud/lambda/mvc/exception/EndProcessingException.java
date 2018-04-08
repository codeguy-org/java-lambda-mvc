package com.compellingcode.cloud.lambda.mvc.exception;

import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public class EndProcessingException extends Exception {
	private LambdaResponse response;

	public EndProcessingException(LambdaResponse response) {
		this.response = response;
	}

	public LambdaResponse getResponse() {
		return response;
	}
}
