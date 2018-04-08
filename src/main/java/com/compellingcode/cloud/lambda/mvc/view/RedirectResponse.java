package com.compellingcode.cloud.lambda.mvc.view;

import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

public class RedirectResponse extends LambdaResponse {
	
	public RedirectResponse(String url, boolean permanent) {
		if(permanent)
			setStatusCode("301");
		else
			setStatusCode("302");
		
		setHeader("Location", url);
	}

	@Override
	public String getBody() throws LambdaResponseException {
		return null;
	}

}
