package com.compellingcode.cloud.lambda.mvc.endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
	String[] value();
	int method() default RequestMethod.ANY;
}
