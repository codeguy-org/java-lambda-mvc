package com.compellingcode.cloud.lambda.mvc.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.compellingcode.cloud.lambda.mvc.domain.EndpointCallback;
import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.compellingcode.cloud.lambda.mvc.handler.EndpointTreeNode;

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
		assertEquals(root.toString(), "EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"a\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"b\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/b/{c}/d\", \"callback\": {\"object\": null, \"method\": null}, \"variables\": [\"c\"], \"dynamicNode\": null, \"staticNodes\": {}}}}, \"staticNodes\": {}}}}}}");
	}
	
	@Test
	public void testPathVariables() throws Exception {
		EndpointTreeNode root = new EndpointTreeNode();
		
		EndpointCallback callback = Mockito.mock(EndpointCallback.class);
		
		root.parse("/a/b/{c}/d", callback);
		root.parse("/a/{b}/c/d", callback);
		root.parse("/{a}/{b}/{c}/{d}", callback);
		
		assertEquals(root.toString(), "EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"/{a}/{b}/{c}/{d}\", \"callback\": {\"object\": null, \"method\": null}, \"variables\": [\"a\",\"b\",\"c\",\"d\"], \"dynamicNode\": null, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {}}, \"staticNodes\": {\"a\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"c\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/{b}/c/d\", \"callback\": {\"object\": null, \"method\": null}, \"variables\": [\"b\"], \"dynamicNode\": null, \"staticNodes\": {}}}}}}, \"staticNodes\": {\"b\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": EndpoingTreeNode {\"path\": \"null\", \"callback\": null, \"variables\": null, \"dynamicNode\": null, \"staticNodes\": {\"d\": EndpoingTreeNode {\"path\": \"/a/b/{c}/d\", \"callback\": {\"object\": null, \"method\": null}, \"variables\": [\"c\"], \"dynamicNode\": null, \"staticNodes\": {}}}}, \"staticNodes\": {}}}}}}");
		
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
