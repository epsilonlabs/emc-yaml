package org.eclipse.epsilon.emc.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.common.util.StringUtil;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.operations.contributors.IOperationContributorProvider;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

public class YamlModel extends CachedModel<Object> implements IOperationContributorProvider {

	public static final String YAML = "yaml";
	public static final String PROPERTY_FILE = "file";
	protected File file;
	protected Object yamlContent;
	protected Node yamlGraph;
	
	public YamlModel() {
		propertyGetter = new YamlPropertyGetter(this);
		propertySetter = new YamlPropertySetter(this);
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public Object getEnumerationValue(String enumeration, String label) throws EolEnumerationValueNotFoundException {
		return null;
	}

	@Override
	public String getTypeNameOf(Object instance) {
		/*
		if(instance instanceof ArrayList) // sequenceNode {
			return YamlProperty.PROPERTY_SEQUENCE + "_" + 
		}
		*/
		
		return instance.getClass().getName();
	}

	@Override
	public Object getTypeOf(Object instance) {
		return instance.getClass();
	}
	
	@Override
	public Object getElementById(String id) {
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		
	}

	@Override
	public boolean owns(Object instance) {
		return true;
	}

	@Override
	public boolean isInstantiable(String type) {
		return hasType(type);
	}

	@Override
	public boolean hasType(String type) {
		//return YAML.equals(type);
		return true;
	}

	@Override
	public boolean store(String location) {
		try {
			Yaml yaml = new Yaml();
		    FileWriter writer = new FileWriter(this.file);
		    yaml.dump(this.yamlContent, writer);
		    return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean store() {
		if (file != null) {
			return store(file.getAbsolutePath());
		}
		return false;
	}

	@Override
	protected Collection<Object> allContentsFromModel() {
		return YamlNodeCollector.collectAllNodes(this.yamlContent);
	}

	@Override
	protected Collection<Object> getAllOfTypeFromModel(String type) throws EolModelElementTypeNotFoundException {
		/*
		if(!YAML.equalsIgnoreCase(type)) {
			throw new EolModelElementTypeNotFoundException(this.name, type);
		}
		*/
		
		return YamlNodeCollector.getAllNodesOfType(type, allContents(), this.name);
	}
	
	

	@Override
	protected Collection<Object> getAllOfKindFromModel(String kind) throws EolModelElementTypeNotFoundException {
		/*
		if(!YAML.equalsIgnoreCase(kind)) {
			throw new EolModelElementTypeNotFoundException(this.name, kind);
		}
		*/
		return getAllOfTypeFromModel(kind);
	}

	@Override
	protected Object createInstanceInModel(String type) throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
		
		YamlProperty yamlProperty = YamlProperty.parse(type);
		
		if (yamlProperty == null) {
			throw new EolModelElementTypeNotFoundException(this.getName(), type);
		}
		
	    Map<String, Object> map = new LinkedHashMap<String, Object>();
	    map.put(yamlProperty.getProperty(), null);
		
		return map.entrySet().iterator().next();
	}

	@Override
	protected void disposeModel() {
		yamlContent = null;	
	}

	@Override
	protected boolean deleteElementInModel(Object instance) throws EolRuntimeException {
		return false;
	}

	@Override
	protected Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
		return type;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		return Collections.singleton(getTypeNameOf(instance));
	}

	
	@Override
	protected synchronized void loadModel() throws EolModelLoadingException {
		try {
			if (this.file != null) {
				Yaml yaml = new Yaml();
		    	try(FileInputStream fileStream = new FileInputStream(this.file); 
		    		InputStreamReader inputStreamReader = new InputStreamReader(fileStream);) {
		    			    		
		        	this.yamlGraph = yaml.compose(inputStreamReader);
		    	}
		    	
		    	try(InputStream inputStream = new FileInputStream(this.file);) {
		        	this.yamlContent = yaml.load(inputStream);
		    	}
		    		    		    	
			}
		}
		catch (Exception ex) {
			throw new EolModelLoadingException(ex, this);
		}
	}
	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		super.load(properties, resolver);
		String filePath = properties.getProperty(PROPERTY_FILE);
		if (!StringUtil.isEmpty(filePath)) {
			file = new File(resolver != null ? resolver.resolve(filePath) : filePath);
		}
		load();
	}

	@Override
	public OperationContributor getOperationContributor() {
		return null;
	}
	
}
