package com.compellingcode.cloud.lambda.mvc.enums;

public enum ContentType {
	UNKNOWN("unknown"),
	MULLTIPART_FORM_DATA("multipart/form-data");
	
	private String mime; 
	
	private ContentType(String mime) {
		this.mime = mime;
	}
	
	public static ContentType resolveType(String mime) {
		if(mime == null)
			return ContentType.UNKNOWN;
		
		for(ContentType type : ContentType.values()) {
			if(type.mime.toLowerCase().equals(mime.toLowerCase())) {
				return type;
			}
		}
		
		return ContentType.UNKNOWN ;
	}
}
