package org.eclipse.epsilon.emc.yaml;

import java.util.Map;

public class YamlNodeParent {

	protected Map parentNode;
	protected boolean hasParent;
	
	public Map getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(Map parentNode) {
		this.parentNode = parentNode;
	}
	
	public boolean hasParent() {
		return hasParent;
	}
	
	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}
}