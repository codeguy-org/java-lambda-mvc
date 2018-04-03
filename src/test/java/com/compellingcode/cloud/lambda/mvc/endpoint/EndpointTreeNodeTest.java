package com.compellingcode.cloud.lambda.mvc.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;

import junit.framework.TestCase;

public class EndpointTreeNodeTest extends TestCase {
	
	
	@Before
	public void setup() {
		
	}

	@Test
	public void testParsingRoot() throws Exception {
		EndpointTreeNode root = new EndpointTreeNode();
		
		EndpointCallback callback = Mockito.mock(EndpointCallback.class);
		
		root.parse("/a/b/{c}/d", callback, 0);
		assertEquals(root.toString(), "EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"a\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"b\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/b/{c}/d\", \"callback1\": {\"object\": null, \"method\": null, \"callback2\": {\"object\": null, \"method\": null, \"callback4\": {\"object\": null, \"method\": null, \"callback8\": {\"object\": null, \"method\": null, \"variables\": [\"c\"], \"dynamicNode\": null, \"staticNodes\": {}}}}, \"staticNodes\": {}}}}}}");
	}
	
	@Test
	public void testPathVariables() throws Exception {
		EndpointTreeNode root = new EndpointTreeNode();
		
		EndpointCallback callback = Mockito.mock(EndpointCallback.class);
		
		root.parse("/a/b/{c}/d", callback, 0);
		root.parse("/a/{b}/c/d", callback, 0);
		root.parse("/{a}/{b}/{c}/{d}", callback, 0);
		
		assertEquals(root.toString(), "EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"/{a}/{b}/{c}/{d}\", \"callback1\": {\"object\": null, \"method\": null, \"callback2\": {\"object\": null, \"method\": null, \"callback4\": {\"object\": null, \"method\": null, \"callback8\": {\"object\": null, \"method\": null, \"variables\": [\"a\",\"b\",\"c\",\"d\"], \"dynamicNode\": null, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {\"a\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"c\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/{b}/c/d\", \"callback1\": {\"object\": null, \"method\": null, \"callback2\": {\"object\": null, \"method\": null, \"callback4\": {\"object\": null, \"method\": null, \"callback8\": {\"object\": null, \"method\": null, \"variables\": [\"b\"], \"dynamicNode\": null, \"staticNodes\": {}}}}}}, \"staticNodes\": {\"b\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback1\": null, \"callback2\": null, \"callback4\": null, \"callback8\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/b/{c}/d\", \"callback1\": {\"object\": null, \"method\": null, \"callback2\": {\"object\": null, \"method\": null, \"callback4\": {\"object\": null, \"method\": null, \"callback8\": {\"object\": null, \"method\": null, \"variables\": [\"c\"], \"dynamicNode\": null, \"staticNodes\": {}}}}, \"staticNodes\": {}}}}}}");
		
		RequestProcessor rp = root.search("/a/b/x/d", 1);
		
		assertEquals(rp.getVariables().size(), 1);
		assertEquals(rp.getVariables().get("c"), "x");
		
		rp = root.search("/a/x/c/d", 1);
		
		assertEquals(rp.getVariables().size(), 1);
		assertEquals(rp.getVariables().get("b"), "x");
		
		rp = root.search("/a/x/y/d", 1);
		
		assertEquals(rp.getVariables().size(), 4);
		assertEquals(rp.getVariables().get("a"), "a");
		assertEquals(rp.getVariables().get("b"), "x");
		assertEquals(rp.getVariables().get("c"), "y");
		assertEquals(rp.getVariables().get("d"), "d");
	}

	
}
