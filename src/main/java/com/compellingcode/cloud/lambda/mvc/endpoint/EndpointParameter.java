package com.compellingcode.cloud.lambda.mvc.endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EndpointParameter {
	ParameterType type();
	String name() default "";
}
