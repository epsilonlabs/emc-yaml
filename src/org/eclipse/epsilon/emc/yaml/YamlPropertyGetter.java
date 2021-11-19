package org.eclipse.epsilon.emc.yaml;

import java.util.ArrayList;
import java.util.Map;
import org.eclipse.epsilon.eol.exceptions.EolIllegalPropertyException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;

public class YamlPropertyGetter extends JavaPropertyGetter {

	protected YamlModel model;
	
	public YamlPropertyGetter(YamlModel model) {
		this.model = model;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
				
		if("value".equals(property)) {
			if (object instanceof Map.Entry) {
				return ((Map.Entry) object).getValue();
			}
			else if (object instanceof ArrayList) {
				return YamlNodeCollector.getValueOfNodes((ArrayList) object);
			}
			else {
				throw new EolIllegalPropertyException(object, property, context);
			}
		}
		
		if (object instanceof Map.Entry) {
			return YamlNodeCollector.collectNodesOfType(((Map.Entry) object).getValue(), property, model.getName());
		} 
		else if (object instanceof ArrayList) {
			return YamlNodeCollector.collectNodesOfType(object, property, model.getName());
		}
			
		
		return super.invoke(object, property, context);
		
	}
	
}
