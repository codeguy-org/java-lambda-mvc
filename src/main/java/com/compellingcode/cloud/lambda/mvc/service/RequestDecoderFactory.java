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
			
			String boundary = null;
			
			for(int i = 1; i < parts.length; i++) {
				String[] subparts = parts[i].split("=", 2);
				if(subparts.length < 2)
					continue;
				
				if("boundary".equalsIgnoreCase(subparts[0].trim())) {
					boundary = subparts[1].trim();
					break;
				}
			}
			
			if(boundary == null)
				throw new InvalidContentTypeException("No boundary defined");
			
			return new MultipartFormDataRequestDecoder(boundary);
		} else if(ct == ContentType.UNKNOWN) {
			return new RawRequestDecoder();
		} else { 
			return new RawRequestDecoder();
		}
	}

}
