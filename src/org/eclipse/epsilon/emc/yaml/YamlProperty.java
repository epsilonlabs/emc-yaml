package org.eclipse.epsilon.emc.yaml;

public class YamlProperty {

	private String property;
	private YamlPropertyType type;
	private YamlPropertyDataType dataType;
	
	public static final char PROPERTY_MAPPING = 'm';
	public static final char PROPERTY_SEQUENCE = 'l';
	public static final char PROPERTY_SCALAR = 'r';
	public static final char PROPERTY_STRING = 's';
	public static final char PROPERTY_INTEGER = 'i';
	public static final char PROPERTY_FLOAT = 'f';
	public static final char PROPERTY_BOOLEAN = 'b';
	public static final char PROPERTY_TIMESTAMP = 't';
	public static final char PROPERTY_DATE = 'd';
	
	public static YamlProperty parse(String property) {
		
		YamlProperty yamlProperty = new YamlProperty();	
		
		if (propertyHasPrefix(property, PROPERTY_SCALAR) || propertyHasPrefix(property, PROPERTY_STRING) || 
			propertyHasPrefix(property, PROPERTY_INTEGER) || propertyHasPrefix(property, PROPERTY_FLOAT) ||
			propertyHasPrefix(property, PROPERTY_BOOLEAN) || propertyHasPrefix(property, PROPERTY_TIMESTAMP) ||
			propertyHasPrefix(property, PROPERTY_DATE)) {
			
			yamlProperty.type = YamlPropertyType.Scalar;
			yamlProperty.dataType = dataTypeFor(property.charAt(0));
		}
		else if (propertyHasPrefix(property, PROPERTY_MAPPING)) {
			yamlProperty.type = YamlPropertyType.Mapping;
		} else if (propertyHasPrefix(property, PROPERTY_SEQUENCE)) {
			yamlProperty.type = YamlPropertyType.Sequence;
		} else {
			yamlProperty = null;
		}
		
		if (yamlProperty != null) {
			yamlProperty.property = property.substring(2);
		}
		
		return yamlProperty;
	}
	
	public Object cast(String value) {
		value = value.trim();
		
		if (dataType == YamlPropertyDataType.BOOLEAN) {
			return Boolean.parseBoolean(value);
		}
		else if (dataType == YamlPropertyDataType.INTEGER) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException ex) {
				return 0;
			}
		}
		else if (dataType == YamlPropertyDataType.FLOAT) {
			try {
				return Float.parseFloat(value);
			}
			catch (NumberFormatException ex) {
				return 0.0f;
			}
		}
		else {
			return value;
		}
	}
	
	private static boolean propertyHasPrefix(String property, char prefix) {
		return (property.charAt(0) == prefix) && (property.charAt(1) == '_');
	}
	
	private static YamlPropertyDataType dataTypeFor(char prefix) {
		YamlPropertyDataType dataType = null;
		switch(prefix)
        {  
        	case PROPERTY_INTEGER:
        		dataType = YamlPropertyDataType.INTEGER;
				break;
        	case PROPERTY_FLOAT:
        		dataType = YamlPropertyDataType.FLOAT;
				break;
        	case PROPERTY_BOOLEAN: 
        		dataType = YamlPropertyDataType.BOOLEAN;
				break; 
        	case PROPERTY_TIMESTAMP: 
        		dataType = YamlPropertyDataType.TIMESTAMP;
				break;
        	case PROPERTY_DATE: 
        		dataType = YamlPropertyDataType.DATE;
				break;
			default: 
				dataType = YamlPropertyDataType.STRING; 
         }
		return dataType;
	}

	public String getProperty() {
		return property;
	}

	public YamlPropertyType getType() {
		return type;
	}

	public YamlPropertyDataType getDataType() {
		return dataType;
	}
	
}
