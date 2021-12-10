package org.eclipse.epsilon.emc.yaml;

import java.util.Map;

public class YamlObject {

	protected Object value;
	protected Map parentNode;
	protected boolean isFound;
	
	public YamlObject(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public Map getParentNode() {
		return parentNode;
	}

	public void setParentNode(Map parentNode) {
		this.parentNode = parentNode;
	}
	
	public void setIsFound(boolean isFound) {
		this.isFound = isFound;
	}
	
	public boolean isFound() {
		return (this.isFound || (this.parentNode != null));
	}
}