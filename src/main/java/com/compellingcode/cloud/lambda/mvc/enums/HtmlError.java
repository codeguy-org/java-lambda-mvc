package com.compellingcode.cloud.lambda.mvc.enums;

public enum HtmlError {
	BadRequest400 (400, "Bad Request", "Your request cannot be processed as submitted."),
	Unauthorized401 (401, "Unauthorized", "This resource requires authentication."),
	AccessDenied403 (403, "Access Denied", "This resource requires authentication."),
	NotFound404 (404, "Resource Not Found", "The requested resource could not be found."),
	Unavailable500 (500, "Page Currently Unavailable", "An internal error occurred."),
	NotImplemented501 (501, "Not Implemented", "The server does not recognize the request method."),
	Unavailable502 (502, "Page Currently Unavailable", "An internal error occurred."),
	Unavailable503 (503, "Page Currently Unavailable", "An internal error occurred.")
	;
	
	private int errorNumber;
	private String errorName;
	private String errorMessage;
	
	private HtmlError(int errorNumber, String errorName, String errorMessage) {
		this.errorNumber = errorNumber;
		this.errorName = errorName;
		this.errorMessage = errorMessage;
	}
	
	public static HtmlError find(int errorNumber) {
		for(HtmlError error : HtmlError.values()) {
			if(error.errorNumber == errorNumber)
				return error;
		}
		
		return Unavailable500;
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public String getErrorName() {
		return errorName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
