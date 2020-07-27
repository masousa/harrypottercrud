package br.com.teste.harryPotter.payload;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.teste.harryPotter.utils.Utils;

public class ConstructorObject {
	public static final Logger log = LogManager.getLogger(ConstructorObject.class);

	private Object entidadeTO;
	private Object entidade;
	public ConstructorObject(Object entidadeTO, Object entidade) {
		this.entidadeTO = entidadeTO;
		this.entidade = entidade;
	}
	
	
	@SuppressWarnings("unused")
	public Object construirEntidadeComposta()  {
		Arrays.asList(entidadeTO.getClass().getDeclaredFields()).forEach(field ->{
			try {
				String nameEntidadeField=field.getName();
				boolean isData =false;
				boolean isEnumValue = false;
				boolean classe = false;
				
				if(field.isAnnotationPresent(FieldProperties.class)) {
					FieldProperties properties = field.getAnnotation(FieldProperties.class);
					if(!StringUtils.isEmpty(properties.name())) {
						nameEntidadeField= properties.name();
					}
					isData = properties.isDateValue();
					isEnumValue = properties.isEnumValue();
					classe = properties.isComposedValue();
				}
				field.setAccessible(true);
				Field campoEntidade = entidade.getClass().getDeclaredField(nameEntidadeField);
				Object value = field.get(entidadeTO);
				if(null!=value) {
					changeValueForEntidade(isData, classe, isEnumValue, campoEntidade, value);
					
				}
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
				log.error(e.getMessage(),e);
			} 


		});
		return entidade;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void changeValueForEntidade(boolean isData, boolean classe, boolean isEnumValue, Field campoEntidade, Object value)
			throws IllegalAccessException, ClassNotFoundException, InstantiationException {
		campoEntidade.setAccessible(true);
		// Conversão do valor de long para Date
		if(isData ) {
			campoEntidade.set(entidade, new Date((long) value));
		
		}else if(classe) {
			Class resultado = Class.forName(campoEntidade.getType().getName());
			Object instanceResultado = resultado.newInstance();
			ConstructorObject co = new ConstructorObject(value, instanceResultado);
			
			campoEntidade.set(entidade, co.construirEntidadeComposta());
		}else if(isEnumValue) {
			campoEntidade.set(entidade, Enum.valueOf((Class<Enum>) campoEntidade.getType(), value.toString()));
		}else {
			campoEntidade.set(entidade, value);
		}
	}
	
	public Object construirTOComposto() {
		Arrays.asList(entidade.getClass().getDeclaredFields()).forEach(entityField ->{
			try {
				Field toField = Utils.findNameFromEntity( entidadeTO.getClass(), entityField.getName());
				if(null!=toField) {
					entityField.setAccessible(true);
					Object value = entityField.get(entidade);
					boolean excludeFromConversion = false;
					if(null!=value) {
						toField.setAccessible(true);
						if(toField.isAnnotationPresent(FieldProperties.class)) {
							FieldProperties field = toField.getAnnotation(FieldProperties.class);
							excludeFromConversion = field.excludeFromConvertionForTO();
						}
						if(!excludeFromConversion) {
							changeValueForTO(toField, value);
						}


					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException | ClassNotFoundException e) {
				log.error(e.getMessage(),e);
				
			}
		});
		return entidadeTO;
	}


	@SuppressWarnings("rawtypes")
	private void changeValueForTO(Field toField, Object value)
			throws IllegalAccessException, ClassNotFoundException, InstantiationException {
		if(value instanceof Date) {
			toField.set(entidadeTO, ((Date)value).getTime());
		}else if(value.getClass().isAnnotationPresent(TOObject.class)) {
			TOObject to = entidade.getClass().getAnnotation(TOObject.class);
			Class classeTO =  Class.forName(to.TOClass().getName());
			Object objeto = classeTO.newInstance();
			ConstructorObject co = new ConstructorObject(objeto, value);
			toField.set(entidadeTO, co.construirTOComposto());
		}	else if(value instanceof Enum){
			toField.set(entidadeTO, ((Enum)value).name());
			
		}else {
			toField.set(entidadeTO, value);
		}
	}
	
	public Object getEntidadeTO() {
		return entidadeTO;
	}
	public void setEntidadeTO(Object entidadeTO) {
		this.entidadeTO = entidadeTO;
	}
	public Object getEntidade() {
		return entidade;
	}
	public void setEntidade(Object entidade) {
		this.entidade = entidade;
	}
	
	
	
	

}
