package com.compellingcode.cloud.lambda.mvc.enums;

public enum MimeType {
	HTML("text/html", new String[] {"html"}),
	JSON("application/json", new String[] {"json"}),
	APPLICATION_OCTETSTREAM("application/octet-stream", new String[] {}),
	JPG("image/jpeg", new String[] {"jpg", "jpeg"}),
	GIF("image/gif", new String[] {"gif"}),
	PNG("image/png", new String[] {"png"}),
	CSS("text/css", new String[] {"css"}),
	JS("application/javascript", new String[] {"js"}),
	TXT("plain/text", new String[] {"txt"})
	;
	
	private String type;
	private String[] extensions;
	
	private MimeType(String type, String[] extensions) {
		this.type = type;
		this.extensions = extensions;
	}
	
	public String getType() {
		return type;
	}
	
	public static MimeType findType(String extension) {
		if(extension == null || extension.length() == 0)
			return MimeType.APPLICATION_OCTETSTREAM;
		
		extension = extension.toLowerCase();
		
		for(MimeType t : MimeType.values()) {
			for(String ext : t.extensions) {
				if(ext.equals(extension))
					return t;
			}
		}
		
		return MimeType.APPLICATION_OCTETSTREAM;
	}
}
