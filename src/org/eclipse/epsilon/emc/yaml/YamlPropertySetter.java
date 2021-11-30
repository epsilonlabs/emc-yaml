package org.eclipse.epsilon.emc.yaml;

import java.util.List;
import java.util.Map.Entry;
import org.eclipse.epsilon.eol.exceptions.EolIllegalPropertyException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.AbstractPropertySetter;

public class YamlPropertySetter extends AbstractPropertySetter {

	protected YamlModel model;
	
	public YamlPropertySetter(YamlModel model) {
		this.model = model;
	}
	
	@Override
	public void invoke(Object object, String property, Object value, IEolContext context) throws EolRuntimeException {	
		if("value".equals(property)) {
			if (object instanceof Entry) {
				if(value instanceof Entry) {
					((Entry) object).setValue(((Entry)value).getValue());
				}
				else {
					((Entry) object).setValue(value);
				}
			}
			else if(object instanceof List) {	
				for(Entry entry: (List<Entry>) object) {
					entry.setValue(value);			
				}
			}	
			else {
				throw new EolIllegalPropertyException(object, property, context);
			}
			return;
		}	
		super.invoke(object, property, context);
	}
}