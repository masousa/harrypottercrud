package br.com.teste.harryPotter.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import br.com.teste.harryPotter.payload.FieldProperties;

public class Utils {
	
	@SuppressWarnings("rawtypes")
	public static Field findNameFromEntity(final Class class1, final String name) {
		List<Field> campos = Arrays.asList(class1.getDeclaredFields());
		for (Field field : campos) {
			String nameClass="";
			if(field.isAnnotationPresent(FieldProperties.class)) {
				nameClass = field.getAnnotation(FieldProperties.class).name();
			}
			if(nameClass.equals(name) || field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	public static String message(String string) {
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		return bundle.getString(string);
	}

}
