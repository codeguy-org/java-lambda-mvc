package com.compellingcode.cloud.lambda.mvc.service;

import com.compellingcode.cloud.lambda.mvc.enums.ContentType;
import com.compellingcode.cloud.lambda.mvc.exception.InvalidContentTypeException;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.MultipartFormDataRequestDecoder;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RawRequestDecoder;
import com.compellingcode.cloud.lambda.mvc.service.requestdecoder.RequestDecoder;

public class RequestDecoderFactory {

	public RequestDecoderFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public RequestDecoder getRequestDecoder(String contentType) throws InvalidContentTypeException {
		String[] parts = contentType.split(";");
		
		if(parts == null || parts.length == 0)
			return new RawRequestDecoder();
		
		ContentType ct = ContentType.resolveType(parts[0].trim());
		
		if(ct == ContentType.MULLTIPART_FORM_DATA) {
			if(parts.length < 2)
				throw new InvalidContentTypeException("Missing boundary");
			
			return new MultipartFormDataRequestDecoder(parts[1].trim());
		} else if(ct == ContentType.UNKNOWN) {
			return new RawRequestDecoder();
		} else { 
			return new RawRequestDecoder();
		}
	}

}
