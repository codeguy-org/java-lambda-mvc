package com.compellingcode.cloud.lambda.mvc.endpoint;

import static org.junit.Assert.*;

import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.compellingcode.cloud.lambda.mvc.view.LambdaResponse;

public class EndpointCallbackTest {

	@Test
	public void test() throws Exception {
		EndpointCallback ec = new EndpointCallback(null, EndpointCallbackTest.class.getDeclaredMethod("testMethod"));
		assertTrue(ec.getParameters().size() == 0);
		
		ec = new EndpointCallback(null, EndpointCallbackTest.class.getDeclaredMethod("testMethod", Context.class, String.class, int.class));
		assertTrue(ec.getParameters().size() == 3);
		assertEquals(ec.getParameters().get(0).getParameterType(), ParameterType.CONTEXT);
		assertEquals(ec.getParameters().get(1).getParameterType(), ParameterType.IP);
		assertEquals(ec.getParameters().get(2).getParameterType(), ParameterType.QUERY_PARAMETER);
		assertEquals(ec.getParameters().get(2).getParameterName(), "id");
	}

	private LambdaResponse testMethod() {
		return null;
	}
	
	private LambdaResponse testMethod(Context context, @EndpointParameter(type=ParameterType.IP) String ip,
			@EndpointParameter(type=ParameterType.QUERY_PARAMETER, name="id") int id) {
		return null;
	}
	
}
