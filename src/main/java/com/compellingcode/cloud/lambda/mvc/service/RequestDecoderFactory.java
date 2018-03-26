package com.compellingcode.cloud.lambda.mvc.service;

import com.compellingcode.cloud.lambda.mvc.enums.ContentType;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.MultipartFormDataRequestDecoder;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RawRequestDecoder;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RequestDecoder;

public class RequestDecoderFactory {

	public RequestDecoderFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public RequestDecoder getRequestDecoder(ContentType contentType) {
		if(contentType == ContentType.MULLTIPART_FORM_DATA) {
			return new MultipartFormDataRequestDecoder();
		} else if(contentType == ContentType.UNKNOWN) {
			return new RawRequestDecoder();
		} else { 
			return new RawRequestDecoder();
		}
	}

}
