package org.eclipse.epsilon.emc.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.List;
import org.yaml.snakeyaml.Yaml;

public class YamlNodeUtility {
	
	public static Collection<Entry> getNodes(Object yamlContent, YamlProperty yamlProperty, boolean areAllNodes) {
		Collection<Entry> nodes = new ArrayList<>();
		setNodes(yamlContent, nodes, yamlProperty, areAllNodes);
		return nodes;
	}
	
	private static void setNodes(Object yamlContent, Collection<Entry> nodes, YamlProperty yamlProperty, boolean areAllNodes) {
		if (yamlContent instanceof List) {
			setNodes((List)yamlContent, nodes, yamlProperty, areAllNodes);
		}
		else if (yamlContent instanceof Map) {
			setNodes((Map)yamlContent, nodes, yamlProperty, areAllNodes);
		}
	}
	
	private static void setNodes(List listNode, Collection<Entry> nodes, YamlProperty yamlProperty, boolean areAllNodes) {
		for (Object node: listNode) {
			if (node instanceof Map) {
				setNodes((Map) node, nodes, yamlProperty, areAllNodes);
			}
		}
	}
	
	private static void setNodes(Map mappingNode, Collection<Entry> nodes, YamlProperty yamlProperty, boolean areAllNodes) {	
		Set<Entry> entries = (Set<Entry>) mappingNode.entrySet();	
		for (Entry entry: entries) {
			setNodes(entry, nodes, yamlProperty, areAllNodes);	
		}			
	}
	
	private static void setNodes(Entry entry, Collection<Entry> nodes, YamlProperty yamlProperty, boolean areAllNodes) {
		Object entryValue = entry.getValue();
		if (entryValue instanceof List) {
			if (yamlProperty.isListNode()) {
				addNodeIfNecessary(entry, yamlProperty, nodes);
			}
			if (areAllNodes) {
				setNodes((List) entryValue, nodes, yamlProperty, areAllNodes);
			}	
		}
		else if (entryValue instanceof Map) {
			if (yamlProperty.isMappingNode()) {
				addNodeIfNecessary(entry, yamlProperty, nodes);
			}
			if (areAllNodes) {
				setNodes((Map) entryValue, nodes, yamlProperty, areAllNodes);
			}
		}
		else {
			if (yamlProperty.isScalarNode()) {
				addNodeIfNecessary(entry, yamlProperty, nodes);
			}
		}
	}
	
	private static void addNodeIfNecessary(Entry entry, YamlProperty yamlProperty, Collection<Entry> nodes) {
		String property = yamlProperty.getProperty();
		if (property == null) {
			nodes.add(entry);
		}
		else {
			if (entry.getKey().equals(property)) {
				nodes.add(entry);
			}
		}
	}
	
	public static Object getQueryResult(Collection<Entry> queryResult, YamlProperty yamlProperty) {	
		if (queryResult == null || queryResult.isEmpty()) {
			return null;
		}
		else {
			return yamlProperty.isMany() ? queryResult : queryResult.iterator().next();
		}
	}
	
	public static boolean isListNode(List list) {
		for (Object object : list) {
			if (!(object instanceof LinkedHashMap))
				return false;
		}
		return true;
	}
	
	public static YamlNodeType getNodeType(String type) {
		YamlNodeType nodeType;
		try {
			nodeType = YamlNodeType.valueOf(type);
		}
		catch(Exception e) {
			nodeType = null;
		}
		return nodeType;
	}
	
	public static YamlNodeType getNodeType(Entry node) {
		Object nodeValue = node.getValue();
		YamlNodeType nodeType = null;
		if (nodeValue instanceof Map) {
			nodeType = YamlNodeType.MappingNode;
		}
		else if (nodeValue instanceof List) {
			nodeType = YamlNodeType.ListNode;
		}
		else {
			nodeType = YamlNodeType.ScalarNode;
		}
		return nodeType;
	}
	
	private static Entry getNode(YamlProperty yamlProperty, Collection<Object> parameters) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		switch(yamlProperty.getType()) {
			case ScalarNode:
				return getScalarNode(yamlProperty, new ArrayList(parameters));
				
			case MappingNode:
				return getMappingNode(yamlProperty, new ArrayList(parameters));
				
			case ListNode:
				return getListNode(yamlProperty, new ArrayList(parameters));
		}
		return map.entrySet().iterator().next();	
	}
	
	public static Entry getNode(String type, String modelName, int indexOfSeparator, Collection<Object> parameters) {
		YamlProperty yamlProperty = YamlProperty.parse(modelName, type, indexOfSeparator);
		return YamlNodeUtility.getNode(yamlProperty, parameters);		
	}
	
	private static Entry getScalarNode(YamlProperty yamlProperty, List parameters) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String nodeName = getNameParameter(yamlProperty, parameters, YamlNodeType.ScalarNode);
		Object nodeValue = getValueParameter(yamlProperty, parameters);
		map.put(nodeName, nodeValue);
		return map.entrySet().iterator().next();
	}
	
	private static Entry getListNode(YamlProperty yamlProperty, List parameters) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String nodeName = getNameParameter(yamlProperty, parameters, YamlNodeType.ListNode);
		Object valueParameter = getValueParameter(yamlProperty, parameters);
		int listSize = (int) YamlTypeConverter.cast(valueParameter.toString(), YamlDataType.INTEGER);
		List list = new ArrayList<>();
		for(int i=0; i < listSize; i++) {
			list.add(new LinkedHashMap());
		}	
		map.put(nodeName, list);
		return map.entrySet().iterator().next();
	}
	
	private static Entry getMappingNode(YamlProperty yamlProperty, List parameters) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String nodeName = getNameParameter(yamlProperty, parameters, YamlNodeType.MappingNode);
		map.put(nodeName, new LinkedHashMap<String, Object>());
		return map.entrySet().iterator().next();
	}
	
	private static String getNameParameter(YamlProperty yamlProperty, List parameters, YamlNodeType yamlNodeType) {
		String nodeName;
		if(yamlProperty.getProperty() != null) {
			nodeName = yamlProperty.getProperty();
		}
		else if(parameters.size() > 0) {
			nodeName = parameters.get(0).toString();
		}
		else {
			nodeName = yamlNodeType.toString();
		}
		return nodeName;
	}
	
	private static Object getValueParameter(YamlProperty yamlProperty, List parameters) {
		int indexOfValueParameter = (yamlProperty.getProperty() != null) ? 0 : 1;
		Object nodeValue = (parameters.size() > indexOfValueParameter) ? parameters.get(indexOfValueParameter) : "";
		return nodeValue;
	}
	
	private static YamlNodeParent getParentNode(Object yamlContent, Entry childNode) {
		YamlNodeParent parentNode = new YamlNodeParent();
		setParentNode(yamlContent, childNode, parentNode);
		return parentNode;
	}
	
	private static void setParentNode(Object yamlContent, Entry childNode, YamlNodeParent parentNode) {
		if (yamlContent instanceof List) {
			setParentNode((List)yamlContent, childNode, parentNode);
		}
		else if (yamlContent instanceof Map) {
			setParentNode((Map)yamlContent, childNode, parentNode);
		}
	}
	
	private static void setParentNode(List listNode, Entry childNode, YamlNodeParent parentNode) {
		for (Object node: listNode) {
			if (!parentNode.hasParent()) {
				if (node instanceof Map) {
					setParentNode((Map) node, childNode, parentNode);
				}
			}
			else {
				break;
			}
		}
	}
	
	private static void setParentNode(Map mappingNode, Entry childNode, YamlNodeParent parentNode) {	
		Set<Entry> entries = (Set<Entry>) mappingNode.entrySet();	
		for (Entry entry: entries) {
			if (entry.equals(childNode)) {
				parentNode.setParentNode(mappingNode);
				parentNode.setHasParent(true);
				break;
			}
			setParentNode(entry, childNode, parentNode);	
		}			
	}
	
	private static void setParentNode(Entry entry, Entry childNode, YamlNodeParent parentNode) {
		Object entryValue = entry.getValue();
		if (entryValue instanceof List) {	
			setParentNode((List) entryValue, childNode, parentNode);
		}
		else if (entryValue instanceof Map) {
			setParentNode((Map) entryValue, childNode, parentNode);
		}
	}
	
	public static boolean deleteNode(Object yamlContent, Object instance) {
		if (instance instanceof Entry) {
			Entry node = (Entry)instance;
			YamlNodeParent parentNode = YamlNodeUtility.getParentNode(yamlContent, node);
			if(parentNode.hasParent()) {
				parentNode.getParentNode().remove(node.getKey());
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean ownsNode(Object yamlContent, Object instance) {
		if (instance instanceof Entry) {
			Entry node = (Entry)instance;
			YamlNodeParent parentNode = YamlNodeUtility.getParentNode(yamlContent, node);
			return parentNode.hasParent();
		}
		else {			
			return (instance instanceof YamlModel) || (yamlContent.equals(instance));	
		}	
	}
	
	public static String getPrefixOfType(YamlNodeType yamlNodeType) {
		String prefix;
		switch(yamlNodeType) {
			case ScalarNode:
				prefix = String.valueOf(YamlProperty.PROPERTY_SCALAR);
				break;
				
			case MappingNode:
				prefix = String.valueOf(YamlProperty.PROPERTY_MAPPING);
				break;
				
			case ListNode:
				prefix = String.valueOf(YamlProperty.PROPERTY_LIST);
				break;
				
			default:
				prefix = null;
		}
		return prefix + YamlProperty.PROPERTY_SEPARATOR;
	}
	
	public static String getTypeNameOf(Entry node) {
		YamlNodeType yamlNodeType = getNodeType(node);
		return getPrefixOfType(yamlNodeType);
	}
	
	public static Object getYamlContent(File file) throws FileNotFoundException, IOException {
		Object yamlContent = null;
		Yaml yaml = new Yaml();		    	
    	try(InputStream inputStream = new FileInputStream(file)) {
        	yamlContent = yaml.load(inputStream);
    	}
    	return yamlContent;
    	//return (yamlContent != null) ? yamlContent : new LinkedHashMap<String, Object>();
	}
	
	public static void storeYamlContent(File file, Object yamlContent) throws IOException {
		Yaml yaml = new Yaml();
		try(FileWriter writer = new FileWriter(file)) {
			yaml.dump(yamlContent, writer);
		}
	}
	
	public static Entry getRootNode(Object yamlContent) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put(YamlProperty.PROPERTY_ROOT, yamlContent);
		return map.entrySet().iterator().next();	
	}
}