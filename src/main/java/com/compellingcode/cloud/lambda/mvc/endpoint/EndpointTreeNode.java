package com.compellingcode.cloud.lambda.mvc.endpoint;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.compellingcode.cloud.lambda.mvc.domain.RequestProcessor;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointConflictException;
import com.compellingcode.cloud.lambda.mvc.exception.EndpointVariableMismatchException;
import com.compellingcode.cloud.lambda.mvc.exception.NoMatchingEndpointException;

public class EndpointTreeNode {
	
	private String path = null;
	
	private Map<Integer, EndpointCallback> callbacks;
	
	private EndpointTreeNode dynamicNode = null;
	private Map<String, EndpointTreeNode> staticNodes = new HashMap<String, EndpointTreeNode>();
	private List<String> variables = null;
	
	public EndpointTreeNode() {
		callbacks = new HashMap<Integer, EndpointCallback>();
		callbacks.put(RequestMethod.GET, null);
		callbacks.put(RequestMethod.POST, null);
		callbacks.put(RequestMethod.PUT, null);
		callbacks.put(RequestMethod.DELETE, null);
	}
	
	public void parse(String path, EndpointCallback callback, int method) throws EndpointConflictException {
		List<String> parts = splitifyPath(path);
		parse("/", parts, callback, new ArrayList<String>(), method);
	}
	

	protected void parse(String path, List<String> parts, EndpointCallback callback, List<String> variables, int method) throws EndpointConflictException {
		if(parts.size() == 0) {
			if(method == 0) {
				for(EndpointCallback ec : this.callbacks.values()) {
					if(ec != null)
						throw new EndpointConflictException("Endpoint (" + path + ") conflicts with Endpoint (" + this.path + ")");
				}
			} else {
				if(this.callbacks.get(method) != null)
					throw new EndpointConflictException("Endpoint (" + path + ") conflicts with Endpoint (" + this.path + ")");
			}
			
			this.path = path;
			this.variables = variables;
			
			if(method == 0) {
				for(int m : this.callbacks.keySet())
					this.callbacks.put(m, callback);
			} else {
				this.callbacks.put(method, callback);
			}
		} else {
			String part = parts.remove(0);
			String variable = variableName(part);
			
			if("/".equals(path))
				path = "";
			
			if(variable != null) {
				variables.add(variable);
				
				if(dynamicNode == null)
					dynamicNode = new EndpointTreeNode();
				
				dynamicNode.parse(path + "/" + part, parts, callback, variables, method);
			} else {
				if(!staticNodes.containsKey(part))
					staticNodes.put(part, new EndpointTreeNode());
				
				staticNodes.get(part).parse(path + "/" + part,  parts, callback, variables, method);
			}
		}
	}
	
	public RequestProcessor search(String path, int method) throws NoMatchingEndpointException, EndpointVariableMismatchException {
		List<String> parts = splitifyPath(path);
		return search(parts, new ArrayList<String>(), method);
	}
	
	public RequestProcessor search(List<String> parts, List<String> values, int method) throws NoMatchingEndpointException, EndpointVariableMismatchException {
		if(parts.size() == 0) {
			if(callbacks.get(method) == null)
				throw new NoMatchingEndpointException();
			
			if(variables.size() != values.size())
				throw new EndpointVariableMismatchException();
			
			Map<String, String> vars = new HashMap<String, String>();
			
			for(int i = 0; i < variables.size(); i++) {
				vars.put(variables.get(i), values.get(i));
			}
			
			return new RequestProcessor(callbacks.get(method), vars);
		} else {
			try {
				return getStaticProcessor(parts, values, method);
			} catch(NoMatchingEndpointException ex) {
				return getDynamicProcessor(parts, values, method);
			} catch(EndpointVariableMismatchException mex) {
				try {
					return getDynamicProcessor(parts, values, method);
				} catch(NoMatchingEndpointException ex) {
					throw mex;
				}
			}
		}
	}
	
	private RequestProcessor getDynamicProcessor(List<String> parts, List<String> values, int method) throws EndpointVariableMismatchException, NoMatchingEndpointException {
		if(dynamicNode != null) {
			String part = parts.remove(0);
			values.add(part);
			try {
				return dynamicNode.search(parts, values, method);
			} catch(EndpointVariableMismatchException ex) {
				values.remove(values.size() - 1);
				parts.add(0, part);
				throw ex;
			} catch(NoMatchingEndpointException ex) {
				values.remove(values.size() - 1);
				parts.add(0, part);
				throw ex;
			}
		} else {
			throw new NoMatchingEndpointException();
		}
	}
	
	private RequestProcessor getStaticProcessor(List<String> parts, List<String> values, int method) throws EndpointVariableMismatchException, NoMatchingEndpointException {
		if(staticNodes.containsKey(parts.get(0))) {
			String part = parts.remove(0);
			try {
				return staticNodes.get(part).search(parts, values, method);
			} catch(NoMatchingEndpointException ex) {
				parts.add(0, part);
				throw ex;
			} catch(EndpointVariableMismatchException ex) {
				parts.add(0, part);
				throw ex;
			}
		} else {
			throw new NoMatchingEndpointException();
		}
	}
	
	private String variableName(String part) {
		if(part == null || part.length() < 3)
			return null;
		
		if(part.charAt(0) != '{')
			return null;
		
		if(part.charAt(part.length() - 1) != '}')
			return null;
		
		return part.substring(1, part.length() - 1).trim();
	}
	
	private List<String> splitifyPath(String path) {
		if(path == null)
			return new ArrayList<String>();
		
		if("/".equals(path))
			return new ArrayList<String>();
		
		path = path.trim();
		
		path = deDotify(path);
		
		path = path.trim();
		
		if(path.length() == 0)
			return new ArrayList<String>();
		
		if("/".equals(path))
			return new ArrayList<String>();
		
		if(path.charAt(0) == '/')
			path = path.substring(1);
		
		path = path.trim();
		
		if(path.length() == 0)
			return new ArrayList<String>();
		
		if("/".equals(path))
			return new ArrayList<String>();
		
		if(path.charAt(path.length() - 1) == '/')
			path = path.substring(0, path.length() - 1);
		
		path = path.trim();

		if("/".equals(path))
			return new ArrayList<String>();
		
		return new ArrayList<String>(Arrays.asList(path.split("/")));
	}
	
	private String deDotify(String path) {
		if(path == null)
			return path;
		
		String newPath = path.replace("..", "");
		
		while(!path.equals(newPath)) {
			path = newPath;
			newPath = path.replace("..", "");
		}
		
		return newPath;
	}

	public String getPath() {
		return path;
	}

	public EndpointCallback getCallback(int method) {
		return callbacks.get(method);
	}

	public List<String> getVariables() {
		return variables;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		
		sw.append("EndpoingTreeNode {");
		sw.append("\"path\": \"");
		sw.append(path);
		sw.append("\"");
		
		for(Entry<Integer, EndpointCallback> entry : callbacks.entrySet()) {
			sw.append(", \"callback" + entry.getKey() + "\": " + callbackToString(entry.getValue()));
		}
		
		sw.append(", \"variables\": ");
		
		if(variables == null) {
			sw.append("null");
		} else {
			sw.append("[");
			
			boolean first = true;
			for(String variable : variables) {
				if(!first)
					sw.append(",");
				
				first = false;
				
				sw.append("\"");
				sw.append(variable);
				sw.append("\"");
			}
			
			sw.append("]");
		}
		
		sw.append(", \"dynamicNode\": ");
		
		if(dynamicNode == null) {
			sw.append("null");
		} else {
			sw.append(dynamicNode.toString());
		}
		
		sw.append(", \"staticNodes\": {");
		
		boolean first = true;
		for(Entry<String, EndpointTreeNode> entry : staticNodes.entrySet()) {
			if(!first)
				sw.append(",");
			
			first = false;
			
			sw.append("\"");
			sw.append(entry.getKey());
			sw.append("\": ");
			sw.append(entry.getValue().toString());
		}
		
		sw.append("}}");
		
		return sw.toString();
	}
	
	private String callbackToString(EndpointCallback callback) {
		if(callback == null)
			return "null";

		StringWriter sw = new StringWriter();

		sw.append("{\"object\": ");
		
		if(callback.getObject() == null) {
			sw.append("null");
		} else {
			sw.append(callback.getObject().getClass().getSimpleName());
		}
		
		sw.append(", \"method\": ");
		
		if(callback.getMethod() == null) {
			sw.append("null");
		} else {
			sw.append(callback.getMethod().getName());
		}		

		return sw.toString();
	}
}
