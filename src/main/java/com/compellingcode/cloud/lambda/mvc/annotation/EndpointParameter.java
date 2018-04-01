package com.compellingcode.cloud.lambda.mvc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.compellingcode.cloud.lambda.mvc.endpoint.ParameterType;

@Retention(RetentionPolicy.RUNTIME)
public @interface EndpointParameter {
	ParameterType type();
	String name() default "";
}
