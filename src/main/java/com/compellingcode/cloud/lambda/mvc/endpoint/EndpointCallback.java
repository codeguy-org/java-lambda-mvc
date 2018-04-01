package com.compellingcode.cloud.lambda.mvc.endpoint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.annotation.EndpointParameter;
import com.compellingcode.cloud.lambda.mvc.domain.LambdaRequest;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;
import com.compellingcode.utils.typeconverter.Converter;
import com.compellingcode.utils.typeconverter.ConverterFactory;

public class EndpointCallback {
	
	private Object object;
	private Method method;
	private List<ParameterInfo> parameters;
	
	public EndpointCallback(Object object, Method method) throws Exception {
		super();
		
		setObject(object);
		setMethod(method);
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

	public List<ParameterInfo> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterInfo> parameters) {
		this.parameters = parameters;
	}

	public void setMethod(Method method) throws Exception {
		this.method = method;
		
		parameters = new ArrayList<ParameterInfo>();
		
		ConverterFactory factory = new ConverterFactory();
		
		Parameter[] parameters = method.getParameters();
		for(Parameter parameter : parameters) {
			EndpointParameter ep = parameter.getAnnotation(EndpointParameter.class);
			if(ep != null) {
				this.parameters.add(new ParameterInfo(factory.getConverter(parameter.getType()), ep.type(), ep.name(), parameter.getType()));
			} else if(parameter.getType() == LambdaRequest.class) {
				this.parameters.add(new ParameterInfo(null, ParameterType.REQUEST_INFORMATION, null, null));
			} else if(parameter.getType() == Context.class) {
				this.parameters.add(new ParameterInfo(null, ParameterType.CONTEXT, null, null));
			} else {
				throw new Exception("Unknown parameter type: " + parameter.getType().getSimpleName());
			}
		}
	}

	public LambdaResponse call(Context context, LambdaRequest request) throws Exception {
		Object[] params = buildParameters(context, request);
		return (LambdaResponse)method.invoke(object, params);
	}
	
	private Object[] buildParameters(Context context, LambdaRequest request) throws Exception {
		int count = parameters.size();
		Object[] params = new Object[count];
		
		for(int i = 0; i < count; i++) {
			ParameterType pt = parameters.get(i).getParameterType();
			
			Object value = null;
			
			if(pt == ParameterType.CONTEXT) {
				value = context;
			} else if(pt == ParameterType.REQUEST_INFORMATION) {
				value = request;
			} else if(pt == ParameterType.IP) {
				value = request.getIp();
			} else if(pt == ParameterType.PATH) {
				value = request.getPath();
			} else if(pt == ParameterType.METHOD) {
				value = request.getMethod();
			} else if(pt == ParameterType.HEADER) {
				value = parameters.get(i).getConverter().convert(request.getHeaders().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else if(pt == ParameterType.PATH_PARAMETER) {
				value = parameters.get(i).getConverter().convert(request.getPathParameters().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else if(pt == ParameterType.POST_PARAMETER) {
				value = parameters.get(i).getConverter().convert(request.getPostParameters().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else if(pt == ParameterType.QUERY_PARAMETER) {
				value = parameters.get(i).getConverter().convert(request.getQueryStringParameters().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else if(pt == ParameterType.REQUEST_PARAMETER) {
				value = parameters.get(i).getConverter().convert(request.getRequestParameters().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else if(pt == ParameterType.STAGE_VARIABLE) {
				value = parameters.get(i).getConverter().convert(request.getStageVariables().get(parameters.get(i).getParameterName()), parameters.get(i).getObjectType());
			} else {
				throw new Exception("Invalid parameter (" + i + ")");
			}
			
			params[i] = value;
		}
		
		return params;
	}
}
