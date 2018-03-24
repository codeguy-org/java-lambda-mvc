package com.compellingcode.cloud.lambda.mvc.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import junit.framework.TestCase;

public class EndpointTreeNodeTest extends TestCase {
	
	
	@Before
	public void setup() {
		
	}

	@Test
	public void testParsingRoot() throws Exception {
		EndpointTreeNode root = new EndpointTreeNode();
		
		EndpointCallback callback = Mockito.mock(EndpointCallback.class);
		
		root.parse("/a/b/{c}/d", callback);
		
		System.out.println(root);
	}
	
	@Test
	public void testPathVariables() throws Exception {
		EndpointTreeNode root = new EndpointTreeNode();
		
		EndpointCallback callback = Mockito.mock(EndpointCallback.class);
		
		root.parse("/a/b/{c}/d", callback);
		root.parse("/a/{b}/c/d", callback);
		root.parse("/{a}/{b}/{c}/{d}", callback);
		
		System.out.println(root);
		
		RequestProcessor rp = root.search("/a/b/x/d");
		
		assertEquals(rp.getVariables().size(), 1);
		assertEquals(rp.getVariables().get("c"), "x");
		
		rp = root.search("/a/x/c/d");
		
		assertEquals(rp.getVariables().size(), 1);
		assertEquals(rp.getVariables().get("b"), "x");
		
		rp = root.search("/a/x/y/d");
		
		assertEquals(rp.getVariables().size(), 4);
		assertEquals(rp.getVariables().get("a"), "a");
		assertEquals(rp.getVariables().get("b"), "x");
		assertEquals(rp.getVariables().get("c"), "y");
		assertEquals(rp.getVariables().get("d"), "d");
	}

	
}
