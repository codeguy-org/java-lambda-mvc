package com.compellingcode.cloud.lambda.mvc.endpoint;

public class RequestMethod {
	// alternative to an enum, since enums can't be used in annotations

	public static final int ANY = 0;
	public static final int GET = 1;
	public static final int POST = 2;
	public static final int PUT = 4;
	public static final int DELETE = 8;
	
	public static int getMethod(String method) {
		if("get".equalsIgnoreCase(method))
			return GET;
		else if("post".equalsIgnoreCase(method))
			return POST;
		else if("put".equalsIgnoreCase(method))
			return PUT;
		else if("delete".equalsIgnoreCase(method))
			return DELETE;
		else
			return ANY;
	}
	
	public static String getMethodName(int method) {
		if(method == GET)
			return "get";
		else if(method == POST)
			return "post";
		else if(method == PUT)
			return "put";
		else if(method == DELETE)
			return "delete";
		else
			return "any";
	}
}
