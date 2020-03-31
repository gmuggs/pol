package edu.gmu.mason.vanilla;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import at.granul.mason.properties.DeclaredPropertied;
import at.granul.mason.properties.Header;
import at.granul.mason.properties.Property;
import at.granul.mason.properties.PropertyElement;
/**
 * General description_________________________________________________________
 * An enumeration to represent agent interests. Letters from A to J represents
 * unique interests while NA represents no interest.
 * 
 * @author Hamdi Kavak (hkavak at gmu.edu), Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

public abstract class AnnotatedPropertied extends DeclaredPropertied {
	private static final long serialVersionUID = 9212119436633046291L;

	protected AnnotatedPropertied() {
		initializationWithDefaultValues();
		declare();
	}
	
	protected abstract void initializationWithDefaultValues();
	
	private void declare() {
		Map<String, List<Field>> propertiesByGroup = new TreeMap<String, List<Field>>();
		Field[] publicFields = this.getClass().getDeclaredFields();

		for (Field field : publicFields) {
			if (field.isAnnotationPresent(EditableProperty.class)) {
				EditableProperty p = field.getAnnotation(EditableProperty.class);
				if (!propertiesByGroup.containsKey(p.group())) {
					propertiesByGroup.put(p.group(), new ArrayList<Field>());
				}
				propertiesByGroup.get(p.group()).add(field);
			}
		}

		List<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (String key : propertiesByGroup.keySet()) {
			properties.add(new Header(key));
			List<Field> fields = propertiesByGroup.get(key);
			for (Field field : fields) {
				EditableProperty p = field.getAnnotation(EditableProperty.class);
				Property element = new Property(field.getName()).title(p.description());
				if(p.readOnly())
					element.setReadOnly(true);
				if (field.getType() == double.class) {
					element.dom(Double.valueOf(p.lower()), Double.valueOf(p.upper()));
				} else if (field.getType() == int.class || field.getType() == long.class) {
					element.dom(Long.valueOf(p.lower()), Long.valueOf(p.upper()));
				} else {
					// do nothing
				}
				properties.add(element);
			}
		}

		this.declaredProperties = properties;
	}
}
