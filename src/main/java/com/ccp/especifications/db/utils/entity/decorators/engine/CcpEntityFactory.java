package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpJsonTransformersDefaultEntityField;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldNotUpdatable;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldTransformer;
import com.ccp.decorators.CcpTextDecorator;
import java.util.stream.Stream;

/**
 * Fábrica responsável por construir a instância final de uma entidade encadeando os decorators
 * configurados via anotações (cache, twin, versionamento, operações, etc.) em ordem de prioridade.
 * Também extrai os campos da entidade a partir da inner class {@code Fields} declarada na classe
 * configuradora.
 */
public class CcpEntityFactory {

	public static Function<Class<?>, String> mainEntityNameProducer = clazz -> {
		String simpleName = clazz.getSimpleName();
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(simpleName);
		CcpTextDecorator ccpStringDecoratorText = ccpStringDecorator.text();
		var toSnakeCase = ccpStringDecoratorText.toSnakeCase();
		String snackCase = toSnakeCase.content;
		int indexOf = snackCase.indexOf("entity");
		int indexOfMais = indexOf + 7;
		String substring = snackCase.substring(indexOfMais);
		return substring;
	};

	public final CcpEntityField[] entityFields;
	public final Class<?> configurationClass;
	public final CcpEntity entityInstance;
	public final boolean hasTwinEntity;

	
	public CcpEntityFactory(Class<?> configurationClass) {
		this.hasTwinEntity = configurationClass.isAnnotationPresent(CcpEntityTwin.class);
		this.entityInstance = getMainEntity(configurationClass);
		this.entityFields = getFields(configurationClass);
		this.configurationClass = configurationClass;
	}
	
	private static CcpEntity getMainEntity(Class<?> configurationClass) {
		CcpEntity entity = getEntity(configurationClass, mainEntityNameProducer);
		return entity;
	}

	/**
	 * Constrói a entidade a partir do {@code configurator}, excluindo os tipos de decorator listados
	 * em {@code decoratorsToAvoid}.
	 */
	public static CcpEntity getCustomEntity(CcpEntityConfigurator configurator, CcpEntityDecoratorTypes... decoratorsToAvoid) {
		CcpEntity entity = configurator.getEntity();
		CcpEntity customEntity = getCustomEntity(entity, decoratorsToAvoid);
		return customEntity;
	}

	/**
	 * Constrói a entidade a partir de uma instância já existente, excluindo os decorators indicados.
	 */
	public static CcpEntity getCustomEntity(CcpEntity entity, CcpEntityDecoratorTypes... decoratorsToAvoid) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		CcpEntity customEntity = getEntity(entityDetails.configurationClass, mainEntityNameProducer, decoratorsToAvoid);
		return customEntity;
	}
	
	/**
	 * Constrói a cadeia de decorators para a classe configuradora, aplicando os tipos presentes nas
	 * anotações em ordem de prioridade, exceto os listados em {@code decoratorsToAvoid}.
	 */
	public static CcpEntity getEntity(Class<?> configurationClass, Function<Class<?>, String> entityNameExtractor, CcpEntityDecoratorTypes... decoratorsToAvoid) {
		
		
		List<CcpEntityDecoratorTypes> avoidedDecorators = Arrays.asList(decoratorsToAvoid);
		
		CcpEntityMetaData entityDetails = new CcpEntityMetaData(configurationClass, entityNameExtractor);
		
		CcpEntity result = new DefaultImplementationEntity(entityDetails);
		CcpEntityDecoratorTypes[] ccpEntityDecoratorTypesValues = CcpEntityDecoratorTypes.values();
		Stream<CcpEntityDecoratorTypes> stream = Arrays.asList(ccpEntityDecoratorTypesValues).stream();
		var filter = stream
				.filter(x -> x.isDecorated(configurationClass));
				var filter2 = filter
				.filter(x -> false == avoidedDecorators.contains(x));

				List<CcpEntityDecoratorTypes> collect = filter2
				
				.collect(Collectors.toList());
		collect.sort((a,b) -> a.priority - b.priority);
		
		for (CcpEntityDecoratorTypes decorator : collect) {
			result = decorator.getEntity(configurationClass, result);
		}
		
		return result;
	}
	
	/**
	 * Extrai os campos da entidade a partir da inner class {@code Fields} declarada em
	 * {@code configurationClass}, construindo um array de {@code CcpEntityField} com os metadados
	 * de cada campo (nome, chave primária, atualizável, transformador).
	 */
	public static CcpEntityField[] getFields(Class<?> configurationClass) {
		
		boolean didNotDeclareFieldsEnum = didNotDeclareFieldsEnum(configurationClass);
		
		if(didNotDeclareFieldsEnum) {
			CcpErrorEntityConfigurationFieldsIsMissing ccpErrorEntityConfigurationFieldsIsMissing = new CcpErrorEntityConfigurationFieldsIsMissing(configurationClass);
			throw ccpErrorEntityConfigurationFieldsIsMissing;
		}
		
		CcpEntityFieldsValidator annotation = configurationClass.getAnnotation(CcpEntityFieldsValidator.class);
		Class<?> entitySchemeValidation = annotation.classReferenceWithTheFields();
		Field[] declaredFields = entitySchemeValidation.getDeclaredFields();
		List<CcpEntityField> list = new ArrayList<>();
		for (Field field : declaredFields) {
			
			Object object;
			try {
				object = field.get(null);
			} catch (Exception e) {
				continue;
			}
			boolean isCcpJsonFieldName = object instanceof CcpJsonFieldName;
			boolean skipThisField = false == isCcpJsonFieldName;
			
			if(skipThisField) {
				continue;
			}
			CcpJsonFieldName ccpJsonFieldName = (CcpJsonFieldName)object;

			String name = (ccpJsonFieldName).name();
			boolean annotationPresent = field.isAnnotationPresent(CcpEntityFieldNotUpdatable.class);

			boolean updatable = false == annotationPresent;
			CcpBusiness transformer = getEntityFieldTransformer(name, field, configurationClass);
			boolean primaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

			CcpEntityField entityField = new CcpEntityField(name, primaryKey, updatable, transformer);
			list.add(entityField);
		}
		int listSize = list.size();

		CcpEntityField[] fields = list.toArray(new CcpEntityField[listSize]);
		
		return fields;
	}
	
	private static  boolean didNotDeclareFieldsEnum(Class<?> configurationClass) {
		
		Class<?>[] declaredClasses = configurationClass.getDeclaredClasses();
		
		boolean hasNoInternalClasses = declaredClasses.length == 0;
		
		if(hasNoInternalClasses) {
			return true;
		}
		
		Class<?> firstClass = declaredClasses[0];
		boolean valor = firstClass.isEnum();

		boolean isNotAnEnum = false == valor;
		
		if(isNotAnEnum) {
			return true;
		}
		
		String simpleName = firstClass.getSimpleName();
		boolean equals = "Fields".equals(simpleName);
		boolean incorrectName = false == equals;
		
		if(incorrectName) {
			return true;
			
		}
		boolean assignableFrom = CcpJsonFieldName.class.isAssignableFrom(firstClass);

		boolean incorrectType = false == assignableFrom;
		if(incorrectType) {
			return true;
		}
		
		return false;
	}

	private static CcpBusiness getEntityFieldTransformer(String name, Field field, Class<?> configurationClass){
		boolean annotationPresent2 = configurationClass.isAnnotationPresent(CcpEntityFieldsTransformer.class);
	
		boolean isNotDecorated = false == annotationPresent2;
		
		if(isNotDecorated) {
			return CcpOtherConstants.DO_NOTHING;
		}
		
		boolean hasCustomEntityFieldTransformer = field.isAnnotationPresent(CcpEntityFieldTransformer.class);
		if(hasCustomEntityFieldTransformer) {
			CcpEntityFieldTransformer annotation = field.getAnnotation(CcpEntityFieldTransformer.class);
			Class<?> value = annotation.value();
			CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(value);
			CcpBusiness transformer = crcd.newInstance();
			return transformer;
		}
		
		CcpEntityFieldsTransformer annotation = configurationClass.getAnnotation(CcpEntityFieldsTransformer.class);
		Class<?> classReferenceWithTheFields = annotation.classReferenceWithTheFields();
		Field declaredField;
		CcpJsonTransformersDefaultEntityField defaultEntityField;
		try {
			declaredField = classReferenceWithTheFields.getDeclaredField(name);
			var get = declaredField.get(null);
			defaultEntityField = (CcpJsonTransformersDefaultEntityField)get;
		} catch (Exception e) {
			return CcpOtherConstants.DO_NOTHING;
		}
		boolean annotationPresent3 = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

		boolean isNotPrimaryKeyField = false == annotationPresent3;

		 if(isNotPrimaryKeyField) {
			 return defaultEntityField;
		 }
		 
		 boolean canBePrimaryKey = defaultEntityField.canBePrimaryKey();
		
		 if(canBePrimaryKey) {
			 return defaultEntityField;
		 }
		 CcpEntityFieldCanNotBePrimaryKey ccpEntityFieldCanNotBePrimaryKey = new CcpEntityFieldCanNotBePrimaryKey(defaultEntityField);

		 throw ccpEntityFieldCanNotBePrimaryKey;
	}

	@SuppressWarnings("serial")
	public static class CcpEntityFieldCanNotBePrimaryKey extends RuntimeException {
		private CcpEntityFieldCanNotBePrimaryKey(CcpJsonTransformersDefaultEntityField defaultEntityField) {
			super("The field '" + defaultEntityField.name() + "' can not be a primary key");
		}
	}

	@SuppressWarnings("serial")
	public static class CcpErrorEntityConfigurationFieldsIsMissing extends RuntimeException {
		private CcpErrorEntityConfigurationFieldsIsMissing(Class<?> configurationClass) {
			super("The class '" + configurationClass.getName() + "' must declare a public static enum called 'FIELDS'");
		}
	}

}
