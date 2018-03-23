package com.compellingcode.cloud.lambda.mvc.view;

public class HtmlLambdaResponse extends LambdaResponse {
	
	public HtmlLambdaResponse() {
	}

	@Override
	public String getBody() {
		String body = "<html><body>Success</body></html>";
		setSize(body.length());
		return body;
	}

}
