package br.com.teste.harryPotter.payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FieldProperties {
	public String name() default "";
	public boolean isDateValue() default false;
	public boolean isIdValue() default false;
	public boolean isEnumValue() default false;
	//public Class<? extends Object>  composedValue() default void.class;
	public boolean isComposedValue() default false;
	public boolean excludeFromConvertionForTO() default false;
}
