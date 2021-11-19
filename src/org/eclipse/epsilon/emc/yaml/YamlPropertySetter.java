package org.eclipse.epsilon.emc.yaml;

import java.util.ArrayList;
import java.util.Map;
import org.eclipse.epsilon.eol.exceptions.EolIllegalPropertyException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.AbstractPropertySetter;
import org.eclipse.epsilon.eol.types.EolSequence;

public class YamlPropertySetter extends AbstractPropertySetter {

	protected YamlModel model;
	
	public YamlPropertySetter(YamlModel model) {
		this.model = model;
	}
	
	@Override
	public void invoke(Object object, String property, Object value, IEolContext context) throws EolRuntimeException {
		
		if("value".equals(property)) {
			if (object instanceof Map.Entry) {
				((Map.Entry) object).setValue(value);
			}
			else if(object instanceof ArrayList) {	
				for(Map.Entry entry: (ArrayList<Map.Entry>) object) {
					entry.setValue(value);			
				}
			}
			else if(object instanceof EolSequence) {
				for(Object item: (EolSequence)object) {
					for(Map.Entry entry: (ArrayList<Map.Entry>) item) {
						entry.setValue(value);			
					}
				}
			}
			else {
				throw new EolIllegalPropertyException(object, property, context);
			}
			model.store();
			return;
		}
		
		super.invoke(object, property, context);
	}
	
}
