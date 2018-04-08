package com.compellingcode.cloud.lambda.mvc.filter;

import java.lang.reflect.Method;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.exception.EndProcessingException;
import com.compellingcode.cloud.lambda.mvc.exception.ExitFilterChainException;
import com.compellingcode.cloud.lambda.mvc.exception.FilterException;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public interface ResponseFilter {
	public LambdaResponse processFilter(LambdaRequest request, LambdaResponse response, Context context, Method method) throws EndProcessingException, ExitFilterChainException, FilterException;
}
