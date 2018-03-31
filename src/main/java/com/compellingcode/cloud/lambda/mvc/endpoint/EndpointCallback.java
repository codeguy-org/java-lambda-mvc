package com.compellingcode.cloud.lambda.mvc.domain;

import java.lang.reflect.Method;

public class EndpointCallback {
	
	private Object object;
	private Method method;
	
	public EndpointCallback(Object object, Method method) {
		super();
		this.object = object;
		this.method = method;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
