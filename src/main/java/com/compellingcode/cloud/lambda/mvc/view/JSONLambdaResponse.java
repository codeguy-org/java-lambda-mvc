package com.compellingcode.cloud.lambda.mvc.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.compellingcode.cloud.lambda.mvc.enums.MimeType;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONLambdaResponse extends LambdaResponse {
	static final Logger logger = LogManager.getLogger(JSONLambdaResponse.class);
	private Object body;

	public JSONLambdaResponse(Object body) {
		this.body = body;
		setMimeType(MimeType.JSON);
	}

	@Override
	public String getBody() throws LambdaResponseException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String result = mapper.writeValueAsString(this.body);
			return result;
		} catch(Exception ex) {
			throw new LambdaResponseException(ex);
		}
	}

}
