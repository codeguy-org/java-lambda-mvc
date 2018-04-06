package com.compellingcode.cloud.lambda.mvc.service.requestdecoder;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.exception.RequestDecoderException;
import com.compellingcode.utils.parser.form.multipart.MultipartFormParser;
import com.compellingcode.utils.parser.form.multipart.domain.FormElement;
import com.compellingcode.utils.parser.form.multipart.exception.InvalidMultipartDataException;
import com.compellingcode.utils.parser.form.multipart.file.FileContainerType;

public class MultipartFormDataRequestDecoder implements RequestDecoder {
	private MultipartFormParser parser;

	public MultipartFormDataRequestDecoder(String boundary) {
		parser = new MultipartFormParser(boundary);
		parser.setFileType(FileContainerType.MEMORY);
	}

	public void decode(byte[] body, LambdaRequest request) throws RequestDecoderException {
		try {
			List<FormElement> elements = parser.parse(new ByteArrayInputStream(body));
			for(FormElement element : elements) {
				if(element.isFile()) {
					request.getPostParameters().put(element.getName(), element);
				} else {
					request.getPostParameters().put(element.getName(), element.getValue());
				}
			}
		} catch (InvalidMultipartDataException e) {
			throw new RequestDecoderException(e);
		}
	}

}
