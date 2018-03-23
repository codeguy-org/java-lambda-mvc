package com.compellingcode.cloud.lambda.mvc.enums;

public enum MimeType {
	HTML("text/html")
	;
	
	private String type;
	
	private MimeType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
