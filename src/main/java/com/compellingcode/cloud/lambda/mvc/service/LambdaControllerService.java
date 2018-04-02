package com.compellingcode.cloud.lambda.mvc.service;

import java.lang.reflect.Method;

import com.compellingcode.cloud.lambda.mvc.endpoint.Endpoint;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointCallback;
import com.compellingcode.cloud.lambda.mvc.endpoint.EndpointTreeNode;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointConflictException;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public class LambdaControllerService {

	public LambdaControllerService() {
		// TODO Auto-generated constructor stub
	}

	
	public void addController(EndpointTreeNode root, Object controller) throws Exception {
		Method[] mm =  controller.getClass().getDeclaredMethods();
		for(Method m : mm) {
			Endpoint e = m.getAnnotation(Endpoint.class);
			if(e != null) {
				for(String path : e.value()) {
					addMethod(root, path, controller, m);
				}
			}
		}
	}
	
	public void addMethod(EndpointTreeNode root, String path, Object controller, String methodName) throws Exception {
		Method[] mm = controller.getClass().getDeclaredMethods();
		for(Method m : mm) {
			if(m.getName().equals(methodName) && m.getReturnType().equals(LambdaResponse.class)) {
				m.setAccessible(true);
				addMethod(root, path, controller, m);
				break;
			}
		}
	}
	
	public void addMethod(EndpointTreeNode root, String path, Object controller, Method method) throws Exception {
		root.parse(path, new EndpointCallback(controller, method));
	}
	
}
