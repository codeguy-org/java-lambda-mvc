package com.compellingcode.cloud.lambda.mvc.view;

import com.compellingcode.cloud.lambda.mvc.enums.HtmlError;
import com.compellingcode.cloud.lambda.mvc.exception.LambdaResponseException;

public class DefaultErrorResponse extends HtmlLambdaResponse {
	private int errorNumber;
	private String errorName;
	private String errorMessage;

	public DefaultErrorResponse(int errorNumber) {
		super("default/ErrorTemplate.tpl");
		
		HtmlError error = HtmlError.find(errorNumber);
		this.errorNumber = errorNumber;
		this.errorName = error.getErrorName();
		this.errorMessage = error.getErrorMessage();
		
		setStatusCode(Integer.toString(errorNumber));
	}
	
	public DefaultErrorResponse(String template, int errorNumber) {
		super(template);
		
		HtmlError error = HtmlError.find(errorNumber);
		this.errorNumber = errorNumber;
		this.errorName = error.getErrorName();
		this.errorMessage = error.getErrorMessage();
	}
	
	@Override
	public String getBody() throws LambdaResponseException {
		setVariable("errorNumber", this.errorNumber);
		setVariable("errorName", this.errorName);
		setVariable("errorMessage", this.errorMessage);
		
		return super.getBody();
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public void setErrorNumber(int errorNumber) {
		this.errorNumber = errorNumber;
	}

	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
