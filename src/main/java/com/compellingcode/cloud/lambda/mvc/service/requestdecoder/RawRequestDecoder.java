package com.compellingcode.cloud.lambda.mvc.service.requestdecoder;

import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.enums.ContentType;

public class RawRequestDecoder implements RequestDecoder {

	public RawRequestDecoder() {
		// TODO Auto-generated constructor stub
	}

	public void decode(byte[] body, LambdaRequest request) {
		request.setBodyType(ContentType.UNKNOWN);
		request.setBody(body);
	}

}
