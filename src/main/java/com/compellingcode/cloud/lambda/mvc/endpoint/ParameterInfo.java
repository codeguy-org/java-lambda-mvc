package com.compellingcode.cloud.lambda.mvc.endpoint;

import java.io.Serializable;
import java.util.List;

import com.compellingcode.utils.typeconverter.Converter;

public class ParameterInfo implements Serializable {
	private Converter converter;
	private ParameterType parameterType;
	private String parameterName;
	private Class<?> objectType;
	
	public ParameterInfo(Converter converter, ParameterType parameterType, String parameterName,
			Class<?> objectType) {
		super();
		this.converter = converter;
		this.parameterType = parameterType;
		this.parameterName = parameterName;
		this.objectType = objectType;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public Class<?> getObjectType() {
		return objectType;
	}

	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}
	
	
}
