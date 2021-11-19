package org.eclipse.epsilon.emc.yaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;

public class YamlNodeCollector {
	
	public static Collection<Object> collectNodesOfType(Object node, String property, String modelName) throws EolModelElementTypeNotFoundException {
		Collection<Object> allNodes = collectAllNodes(node);
		Collection<Object> allNodesOfType = getAllNodesOfType(property, allNodes, modelName);
		return allNodesOfType;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Object> collectAllNodes(Object yamlContent) {
		Collection<Object> allNodes = new ArrayList<>();
		if (yamlContent instanceof LinkedHashMap) {
			allNodes.add(yamlContent);
		}
		else if (yamlContent instanceof ArrayList) {
			allNodes.addAll((Collection<? extends Object>) yamlContent);
		}
		return allNodes;
	}
	
	public static Collection<Object> getAllNodesOfType(String type, Collection<Object> nodes, String modelName) throws EolModelElementTypeNotFoundException {
		
		YamlProperty yamlProperty = YamlProperty.parse(type);
		
		if (yamlProperty == null) {
			throw new EolModelElementTypeNotFoundException(modelName, type);
		}
		
		Collection<Object> allNodesOfType = new ArrayList<>();
		
		for (Object node: nodes) {
			
			Object mappingNode = null;
			
			if (node instanceof LinkedHashMap) {		
				mappingNode = node;
			}
			else if (node instanceof Map.Entry) {
				mappingNode = ((Map.Entry) node).getValue();
			}
			
			switch(yamlProperty.getType())
	        {  
	        	case Scalar:
	        		getScalarNodes((LinkedHashMap) mappingNode, yamlProperty.getProperty(), allNodesOfType);
					break;
	        	case Mapping:
	        		getMappingNodes((LinkedHashMap) mappingNode, yamlProperty.getProperty(), allNodesOfType);
					break;
	        	case Sequence:
	        		getSequenceNodes((LinkedHashMap) mappingNode, yamlProperty.getProperty(), allNodesOfType);
					break;
	        }
		}
		
		return allNodesOfType;
	}
	
	public static ArrayList<Object> getValueOfNodes(ArrayList nodes) {
		ArrayList<Object> values = new ArrayList<>();
		for(Object node: nodes) {
			if(node instanceof Map.Entry) {
				values.add(((Map.Entry) node).getValue());
			}
		}
		return values;
	}
	
	protected static void getSequenceNodes(LinkedHashMap mappingNode, String type, Collection<Object> sequenceNodes) {
		for (Map.Entry<String, Object> nodeTuple : (Set<Map.Entry>)(mappingNode).entrySet()) {
			if (nodeTuple.getValue() instanceof ArrayList) { //sequenceNode
				if (type.equals(nodeTuple.getKey())) {
					sequenceNodes.add(nodeTuple);
				}
				getSequenceNodes((ArrayList) nodeTuple.getValue(), type, sequenceNodes);
			}
			else if (nodeTuple.getValue() instanceof LinkedHashMap) { //mappingNode
				getSequenceNodes((LinkedHashMap) nodeTuple.getValue(), type, sequenceNodes);
			}
		}			
	}
	
	protected static void getSequenceNodes(ArrayList sequenceNode, String type, Collection<Object> sequenceNodes) {
		for (Object node: sequenceNode) {
			getSequenceNodes((LinkedHashMap) node, type, sequenceNodes);
		}
	}
	
	
	protected static void getMappingNodes(LinkedHashMap mappingNode, String type, Collection<Object> mappingNodes) {
		for (Map.Entry<String, Object> nodeTuple : (Set<Map.Entry>)(mappingNode).entrySet()) {
			if (nodeTuple.getValue() instanceof ArrayList) { //sequenceNode
				getMappingNodes((ArrayList) nodeTuple.getValue(), type, mappingNodes);
			}
			else if (nodeTuple.getValue() instanceof LinkedHashMap) { //mappingNode
				if (type.equals(nodeTuple.getKey())) {
					mappingNodes.add(nodeTuple);
				}
				getMappingNodes((LinkedHashMap) nodeTuple.getValue(), type, mappingNodes);
			}
		}
	}
	
	protected static void getMappingNodes(ArrayList sequenceNode, String type, Collection<Object> mappingNodes) {
		for (Object node: sequenceNode) {
			getMappingNodes((LinkedHashMap) node, type, mappingNodes);
		}
	}
		
	protected static void getScalarNodes(LinkedHashMap mappingNode, String type, Collection<Object> scalarNodes) {
		for (Map.Entry<String, Object> nodeTuple : (Set<Map.Entry>)(mappingNode).entrySet()) {
			if (nodeTuple.getValue() instanceof ArrayList) { //sequenceNode
				getScalarNodes((ArrayList) nodeTuple.getValue(), type, scalarNodes);
			}
			else if (nodeTuple.getValue() instanceof LinkedHashMap) { //mappingNode
				getScalarNodes((LinkedHashMap) nodeTuple.getValue(), type, scalarNodes);
			}
			else {
				if (type.equals(nodeTuple.getKey())) {
					scalarNodes.add(nodeTuple);
				}
			}
		}
	}
	
	protected static void getScalarNodes(ArrayList sequenceNode, String type, Collection<Object> scalarNodes) {
		for (Object node: sequenceNode) {
			getScalarNodes((LinkedHashMap) node, type, scalarNodes);
		}
	}
	
}
