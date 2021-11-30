package org.eclipse.epsilon.emc.yaml;

import java.util.Collection;
import java.util.Map.Entry;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.eclipse.epsilon.eol.types.EolModelElementType;

public class YamlPropertyGetter extends JavaPropertyGetter {

	protected YamlModel model;
	
	public YamlPropertyGetter(YamlModel model) {
		this.model = model;
	}
	
	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
		if (object instanceof EolModelElementType) {
			return super.invoke(object, property, context);
		}
		else {
			Object yamlContent = object;
			if (object instanceof Entry) {
				yamlContent = ((Entry)object).getValue();		
				if(property.endsWith("value")) {
					return YamlTypeConverter.getValue(yamlContent, property);
				} 
				else if(property.equals("name")) {
					return ((Entry)object).getKey();
				}
				else if(property.equals("type")) {
					return YamlNodeUtility.getNodeType((Entry)object);
				}
			}
			YamlProperty yamlProperty = YamlProperty.parse(this.model.getName(), property, 2);	
			Collection<Entry> queryResult = YamlNodeUtility.getNodes(yamlContent, yamlProperty, false);			
			return YamlNodeUtility.getQueryResult(queryResult, yamlProperty);		
		}			
	}
}