package com.compellingcode.cloud.lambda.mvc.service.requestdecoder;

import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;

public interface RequestDecoder {
	public void decode(byte[] body, LambdaRequest request);
}
