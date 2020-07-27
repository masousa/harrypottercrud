package br.com.teste.harryPotter.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.annotation.Id;

import br.com.teste.harryPotter.domain.Entidade;
import br.com.teste.harryPotter.payload.ConstructorObject;
import br.com.teste.harryPotter.payload.EntidadeTO;
import br.com.teste.harryPotter.payload.FieldProperties;
import br.com.teste.harryPotter.payload.TO;
import br.com.teste.harryPotter.payload.TOObject;

public abstract class BuilderTOObject <T extends Entidade , S extends EntidadeTO<T>>{
	public static final Logger log = LogManager.getLogger(BuilderTOObject.class);
	private Class<T> entidadeClass;
	private Class<S> entidadeTOClass;
	
	public BuilderTOObject() {
		
	}
	
	
	/**
	 * Método responsável por realizar a conversão de uma {@link EntidadeTO} para uma {@link Entidade}
	 * @param entidadeTO Payload que representa a entidade
	 * @param entidade Entidade a ser preenchida.
	 * @return uma Entidade a partir da representação do Payload da mesma
	 */
	public T buildEntidade(S entidadeTO, T entidade) {

		Arrays.asList(entidadeTO.getClass().getDeclaredFields()).forEach(field ->{
			try {
				String nameEntidadeField=field.getName();
				boolean isData =false;
				boolean isId = false;
				boolean isComposed = false;
				
				if(field.isAnnotationPresent(FieldProperties.class)) {
					FieldProperties properties = field.getAnnotation(FieldProperties.class);
					if(!StringUtils.isEmpty(properties.name())) {
						nameEntidadeField= properties.name();
					}
					isData = properties.isDateValue();
					isComposed = properties.isComposedValue();
					isId = properties.isIdValue();
				}
				field.setAccessible(true);
				Field campoEntidade = entidade.getClass().getDeclaredField(nameEntidadeField);
				Object value = field.get(entidadeTO);
				if(null!=value) {
					realizarConversaoDoCampoParaEntidade(entidade, isData, isId, isComposed, campoEntidade, value);
					
				}
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
				log.error(e.getMessage(),e);
			} 


		});
		
		return entidade;
	}

	
	/***
	 * Método que realiza a conversão do campo da {@link EntidadeTO} para a {@link Entidade}
	 * a ser preenchida.
	 * @param entidade
	 * @param isData
	 * @param isId
	 * @param isComposed
	 * @param campoEntidade
	 * @param value
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	private void realizarConversaoDoCampoParaEntidade(T entidade, boolean isData, boolean isId, boolean isComposed,
			Field campoEntidade, Object value)
			throws IllegalAccessException, ClassNotFoundException, InstantiationException {
		campoEntidade.setAccessible(true);
		// Conversão do valor de long para Date
		if(isData ) {
			campoEntidade.set(entidade, new Date((long) value));
		}else if(isId) {
			campoEntidade.set(entidade, convertTOIDMethod(value));
		}else if(value instanceof EntidadeTO) {
			campoEntidade.set(entidade, ((EntidadeTO)value).build());
		}else if(isComposed) {
			Class resultado = Class.forName(campoEntidade.getType().getName());
			Object instanceResultado = resultado.newInstance();
			ConstructorObject co = new ConstructorObject(value, instanceResultado);
			Object builtedComposedObject = co.construirEntidadeComposta();
			campoEntidade.set(entidade, builtedComposedObject);
		}else if(value instanceof Collection) {
			campoEntidade.set(entidade, extractEntityListValue(isComposed, value));
		}else {
			
			campoEntidade.set(entidade, value);
		}
	}

	/***
	 * Caso o valor represente uma coleção sendo ela um {@link EntidadeTO}} ou uma 
	 * entidade composta este método preenche o valor do mesmo e adiciona este valor
	 * à lista da entidade.
	 * @param isComposed
	 * @param value
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object extractEntityListValue(boolean isComposed, Object value)
			throws InstantiationException, IllegalAccessException {
		Iterator<Object> itObject = ((Collection)value).iterator();
		List<Object> listResult = new ArrayList<>();
		Object firstObject = ((Collection)value).iterator().next();
		while(itObject.hasNext()) {
			Object oValue = itObject.next();
			if(oValue instanceof EntidadeTO) {
			  listResult.add(((EntidadeTO)oValue).build());
			}else if(isComposed) {
				Object newInstance = firstObject.getClass().newInstance();
				ConstructorObject co = new ConstructorObject(oValue, newInstance);
				listResult.add(co.construirEntidadeComposta());
			}else {
				listResult.add(oValue);
			}
		}
		return listResult;
	}

	
	
	/***
	 * Método que transforma uma instância de uma {@link Entidade} em uma instância de uma {@link EntidadeTO}
	 * @param entidade Entidade da aplicação
	 * @param entidadeTO Representação payload da entidade da aplicação
	 * @return transforma uma entidade em uma representação payload de acordo com as anotações presentes na classe payload
	 */
	@SuppressWarnings({ "unchecked" })
	public S buildTO(T entidade, S entidadeTO) {
		Arrays.asList(entidade.getClass().getDeclaredFields()).forEach(entityField ->{
			try {
				Field toField = Utils.findNameFromEntity((Class<S>) entidadeTO.getClass(), entityField.getName());
				if(null!=toField) {
					entityField.setAccessible(true);
					Object value = entityField.get(entidade);
					boolean excludeFromConversion = false;
					if(null!=value) {
						preencherTOComFieldDaEntidade(entidadeTO, entityField, toField, value, excludeFromConversion);


					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				log.error(e.getMessage(),e);
				
			}
		});
		return entidadeTO;
	}


	/***
	 * Método que pega o field da {@link Entidade} associa o mesmo ao campo correspondente da {@link EntidadeTO}
	 * e converte o valor desta entidade ao campo da entidadeTO
	 * @param entidadeTO
	 * @param entityField
	 * @param toField
	 * @param value
	 * @param excludeFromConversion
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private void preencherTOComFieldDaEntidade(S entidadeTO, Field entityField, Field toField, Object value,
			boolean excludeFromConversion) throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		toField.setAccessible(true);
		if(toField.isAnnotationPresent(FieldProperties.class)) {
			FieldProperties field = toField.getAnnotation(FieldProperties.class);
			excludeFromConversion = field.excludeFromConvertionForTO();
		}
		if(!excludeFromConversion) {
			if(value instanceof Date) {
				toField.set(entidadeTO, ((Date)value).getTime());
			}else if(entityField.isAnnotationPresent(Id.class)) {
				toField.set(entidadeTO, convertIDMethodTO(value));
			}else if(value instanceof Entidade) {
				if(value.getClass().isAnnotationPresent(TO.class)) {
					EntidadeTO buildTOObject = buildTOEntity(value);
					toField.set(entidadeTO, buildTOObject);
				}
			}else if(value.getClass().isAnnotationPresent(TOObject.class)) {
				Object construirTOCompostoObject = buildComposedObject(value);
				toField.set(entidadeTO, construirTOCompostoObject);
			}else if(value instanceof Collection) {
				List<Object> listResult = loadListObject(value);
				toField.set(entidadeTO, listResult);
			}	else {
				toField.set(entidadeTO, value);
			}
		}
	}


	/**
	 * Método que converte uma lista de um atributo em uma {@link EntidadeTO} em uma lista de um atributo correspondente 
	 * de um {@link Entidade}
	 * @param value
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> loadListObject(Object value) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		Iterator<Object> listValue = ((Collection)value).iterator();
		List<Object> listResult = new ArrayList<>();
		while(listValue.hasNext()) {
			Object oValue = listValue.next();
			if(oValue instanceof Entidade) {
				listResult.add(buildTOEntity(oValue));

			}else if(value.getClass().isAnnotationPresent(TOObject.class)) {
				listResult.add(buildComposedObject(oValue));
			}else {
				listResult.add(oValue);
			}
			
		}
		return listResult;
	}


	/***
	 * Método que preenche um objeto composto de um atributo de um {@link Entidade} em um objeto composto correspondente
	 * da classe {@link EntidadeTO}
	 * @param value
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	private Object buildComposedObject(Object value)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		TOObject to = value.getClass().getAnnotation(TOObject.class);
		Class classeTO =  Class.forName(to.TOClass().getName());
		Object objeto = classeTO.newInstance();
		ConstructorObject co = new ConstructorObject(objeto, value);
		Object construirTOCompostoObject = co.construirTOComposto();
		return construirTOCompostoObject;
	}


	/***
	 * Converte um objeto de um atributo em uma classe {@link Entidade} e um atributo
	 * correspondente do atributo da classe {@link EntidadeTO}
	 * @param value
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EntidadeTO buildTOEntity(Object value) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		BuilderTOObject builder = getClass().getConstructor().newInstance();
		TO to = value.getClass().getAnnotation(TO.class);
		Class<S>classeTO = (Class<S>) Class.forName(to.TOClass().getName());
		EntidadeTO buildTOObject = builder.buildTO((Entidade) value, classeTO.getConstructor().newInstance());
		return buildTOObject;
	}
	
	
	@SuppressWarnings("unchecked")
	public T createNewInstanceEntidade() {
		try {
			if(null==entidadeClass) {
				entidadeClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			}
			return entidadeClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public S createNewInstanceEntidadeTO() {
		try {
			if(null==entidadeTOClass) {
				entidadeTOClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			}
			return entidadeTOClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	protected abstract Object convertTOIDMethod(Object value);
	protected abstract Object convertIDMethodTO(Object value);

}
