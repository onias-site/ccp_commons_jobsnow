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
import com.ccp.especifications.db.utils.entity.CcpEntity.CcpEntityNoDefinedPrimaryKey;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorators;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpJsonTransformersDefaultEntityField;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldNotUpdatable;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldTransformer;
import com.ccp.decorators.CcpTextDecorator;
import java.util.stream.Stream;

/**
 * Factory responsible for building the final instance of an entity by chaining the decorators
 * configured through annotations (cache, twin, versioning, operations, etc.) in priority order.
 * It also extracts the entity fields from the inner class {@code Fields} declared in the
 * configurator class.
 */
public class CcpEntityFactory {

	/**
	 * Derives the entity name from the simple name of the configurator class, keeping the cost center
	 * prefix and discarding only the {@code Entity} marker. Example:
	 * {@code JnEntityJobsnowError} becomes {@code jn_jobsnow_error}.
	 */
	public static Function<Class<?>, String> mainEntityNameProducer = clazz -> {
		String simpleName = clazz.getSimpleName();
		CcpStringDecorator simpleNameDecorator = new CcpStringDecorator(simpleName);
		CcpTextDecorator simpleNameText = simpleNameDecorator.text();
		var snakeCaseDecorator = simpleNameText.toSnakeCase();
		String snakeCase = snakeCaseDecorator.content;
		int entityMarkerIndex = snakeCase.indexOf("_entity_");
		String costCenterPrefix = snakeCase.substring(0, entityMarkerIndex);
		int entityNameStartIndex = entityMarkerIndex + 8;
		String nameAfterEntityMarker = snakeCase.substring(entityNameStartIndex);
		String entityName = costCenterPrefix + "_" + nameAfterEntityMarker;
		return entityName;
	};

	/** The fields of the entity. */
	public final CcpEntityField[] entityFields;
	/** The configurator class. */
	public final Class<?> configurationClass;
	/** The main entity, fully decorated. */
	public final CcpEntity entityInstance;
	/** Whether the configurator class has {@code @CcpEntityTwin}. */
	public final boolean hasTwinEntity;

	
	/**
	 * Builds the main entity of the configurator class and reads its fields.
	 * @param configurationClass the configurator class
	 */
	public CcpEntityFactory(Class<?> configurationClass) {
		this.hasTwinEntity = configurationClass.isAnnotationPresent(CcpEntityTwin.class);
		this.entityInstance = getMainEntity(configurationClass);
		this.entityFields = getFields(configurationClass);
		this.configurationClass = configurationClass;
		validatePrimaryKey(configurationClass, this.entityFields);
	}

	/**
	 * Refuses an entity that declares no primary key. Every entity needs one: the id of a record is computed from it
	 * ({@code CcpEntity.calculateId}), and that is how save overwrites instead of duplicating and how exists, getOneById
	 * and delete find a record from its data. An entity that wants one record per event declares a generated field as its
	 * key, as {@code jn_async_task} does with {@code messageId}. Since 2026-10-07 the refusal happens here, when the
	 * entity is built (the {@code ENTITY} constant of the configurator, so at the first use of the class and in the tests
	 * of the structure of the entities); before, only at the first read or write.
	 * <p>
	 * It reads the fields of the configurator, not the metadata of the entity: the metadata are completed only after the
	 * construction (the association with the decorated entity), and reading them here breaks the construction of every
	 * entity.
	 * @param configurationClass the configurator class
	 * @param fields the fields read from the configurator
	 * @throws CcpEntityNoDefinedPrimaryKey when the entity declares no primary key
	 */
	private static void validatePrimaryKey(Class<?> configurationClass, CcpEntityField[] fields) {
		for (CcpEntityField field : fields) {
			if(field.primaryKey) {
				return;
			}
		}
		String entityName = mainEntityNameProducer.apply(configurationClass);
		CcpEntityNoDefinedPrimaryKey noPrimaryKeyError = new CcpEntityNoDefinedPrimaryKey(entityName);
		throw noPrimaryKeyError;
	}
	
	/**
	 * Builds the main entity, named by {@link #mainEntityNameProducer}.
	 * @param configurationClass the configurator class
	 * @return the decorated main entity
	 */
	private static CcpEntity getMainEntity(Class<?> configurationClass) {
		CcpEntity entity = getEntity(configurationClass, mainEntityNameProducer);
		return entity;
	}

	/**
	 * Builds the entity from the {@code configurator}, excluding the decorator types listed
	 * in {@code decoratorsToAvoid}.
	 */
	public static CcpEntity getCustomEntity(CcpEntityConfigurator configurator, CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntity entity = configurator.getEntity();
		CcpEntity customEntity = getCustomEntity(entity, decoratorsToAvoid);
		return customEntity;
	}

	/**
	 * Builds the entity from an already existing instance, excluding the given decorators.
	 */
	public static CcpEntity getCustomEntity(CcpEntity entity, CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		CcpEntity customEntity = getEntity(entityDetails.configurationClass, mainEntityNameProducer, decoratorsToAvoid);
		return customEntity;
	}
	
	/**
	 * Builds the decorator chain for the configurator class, applying the types present in the
	 * annotations in priority order, except the ones listed in {@code decoratorsToAvoid}.
	 */
	public static CcpEntity getEntity(Class<?> configurationClass, Function<Class<?>, String> entityNameExtractor, CcpEntityDecoratorType... decoratorsToAvoid) {
		
		List<CcpEntityDecoratorType> avoidedDecorators = Arrays.asList(decoratorsToAvoid);
		
		CcpEntityMetaData entityDetails = new CcpEntityMetaData(configurationClass, entityNameExtractor);
		
		CcpEntity result = new DefaultImplementationEntity(entityDetails);
		CcpEntityDecoratorTypes[] enumDecoratorTypes = CcpEntityDecoratorTypes.values();
		List<CcpEntityDecoratorType> enumDecoratorTypesList = Arrays.asList(enumDecoratorTypes);
		Stream<CcpEntityDecoratorType> decoratorTypesStream = enumDecoratorTypesList.stream();
		var annotatedDecoratorsStream = decoratorTypesStream
				.filter(x -> x.isAnnoted(configurationClass));

				List<CcpEntityDecoratorType> annotatedDecorators = annotatedDecoratorsStream
				
				.collect(Collectors.toList());

		List<CcpEntityDecoratorType> decoratorsToApply = new ArrayList<>(annotatedDecorators);		
		
		List<CcpEntityDecoratorType> customDecorators = getCustomDecorators(configurationClass);
		
		decoratorsToApply.addAll(customDecorators);
		
		List<CcpEntityDecoratorType> filtered = decoratorsToApply.stream()
		.filter(x -> false == avoidedDecorators.contains(x)).collect(Collectors.toList());
		
		filtered.sort((a,b) -> a.getPriority(configurationClass) - b.getPriority(configurationClass));
		
		for (CcpEntityDecoratorType decorator : filtered) {
			result = decorator.getEntity(configurationClass, result);
		}
		
		return result;
	}
	
	/**
	 * Instantiates, through reflection, the decorators listed in {@code @CcpEntityCustomDecorators}.
	 * @param configurationClass the configurator class
	 * @return the custom decorators, or an empty list when the annotation is absent
	 */
	private static List<CcpEntityDecoratorType> getCustomDecorators(Class<?> configurationClass) {
		
		boolean annotationPresent = configurationClass.isAnnotationPresent(CcpEntityCustomDecorators.class);

		boolean isNotDecorated = false == annotationPresent;

		if(isNotDecorated) {
			return new ArrayList<>();
		}
		
		CcpEntityCustomDecorators annotation = configurationClass.getAnnotation(CcpEntityCustomDecorators.class);
		CcpEntityCustomDecorator[] customDecoratorAnnotations = annotation.value();
		List<CcpEntityCustomDecorator> customDecoratorAnnotationsList = Arrays.asList(customDecoratorAnnotations);
		
		List<CcpEntityDecoratorType> customDecorators = 
				customDecoratorAnnotationsList.stream()
				.map(x -> x.value())
				.map(x -> new CcpReflectionConstructorDecorator(x))
				.map(x -> (CcpCustomDecoratorEntity) x.newInstance()).collect(Collectors.toList());
		
		return customDecorators;
	}

	/**
	 * Reads the fields of the entity. The configurator class must declare a nested enum {@code Fields} implementing
	 * {@code CcpJsonFieldName}, but the fields themselves are the static {@code CcpJsonFieldName} fields of the class named
	 * by {@code @CcpEntityFieldsValidator} (which is therefore mandatory): each one becomes a {@code CcpEntityField}
	 * whose primary key flag comes from {@code @CcpEntityFieldPrimaryKey}, whose updatable flag is the absence of
	 * {@code @CcpEntityFieldNotUpdatable} and whose transformer comes from {@code getEntityFieldTransformer}.
	 * @param configurationClass the configurator class
	 * @return the fields of the entity
	 * @throws CcpErrorEntityConfigurationFieldsIsMissing when the {@code Fields} enum is not declared
	 */
	public static CcpEntityField[] getFields(Class<?> configurationClass) {
		
		boolean didNotDeclareFieldsEnum = didNotDeclareFieldsEnum(configurationClass);
		
		if(didNotDeclareFieldsEnum) {
			CcpErrorEntityConfigurationFieldsIsMissing fieldsEnumMissingError = new CcpErrorEntityConfigurationFieldsIsMissing(configurationClass);
			throw fieldsEnumMissingError;
		}
		
		// the fields come from the class of @CcpEntityFieldsValidator, or, without it, from the Fields enum itself (until
		// 2026-10-06 an entity without the annotation broke here with NullPointerException)
		boolean hasFieldsValidator = configurationClass.isAnnotationPresent(CcpEntityFieldsValidator.class);
		Class<?> entitySchemeValidation = hasFieldsValidator
				? configurationClass.getAnnotation(CcpEntityFieldsValidator.class).classReferenceWithTheFields()
				: getFieldsEnum(configurationClass);
		Field[] declaredFields = entitySchemeValidation.getDeclaredFields();
		List<CcpEntityField> entityFieldsList = new ArrayList<>();
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
			CcpJsonFieldName jsonFieldName = (CcpJsonFieldName)object;

			String name = (jsonFieldName).name();
			boolean annotationPresent = field.isAnnotationPresent(CcpEntityFieldNotUpdatable.class);

			boolean updatable = false == annotationPresent;
			CcpBusiness transformer = getEntityFieldTransformer(name, field, configurationClass);
			boolean primaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

			CcpEntityField entityField = new CcpEntityField(name, primaryKey, updatable, transformer);
			entityFieldsList.add(entityField);
		}
		int fieldsCount = entityFieldsList.size();

		CcpEntityField[] fields = entityFieldsList.toArray(new CcpEntityField[fieldsCount]);
		
		return fields;
	}
	
	/**
	 * Looks for the {@code Fields} enum among <b>all</b> the nested types of the configurator class, in
	 * any position. The order returned by {@code getDeclaredClasses()} is unspecified, so
	 * looking only at the first element rejected correct entities just because they declared some other nested
	 * type before {@code Fields}.
	 */
	private static  boolean didNotDeclareFieldsEnum(Class<?> configurationClass) {
		Class<?>[] declaredClasses = configurationClass.getDeclaredClasses();
		for (Class<?> declaredClass : declaredClasses) {
			boolean isTheFieldsEnum = isTheFieldsEnum(declaredClass);
			if(isTheFieldsEnum) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the {@code Fields} enum of the configurator class.
	 * @param configurationClass the configurator class
	 * @return the {@code Fields} enum
	 * @throws CcpErrorEntityConfigurationFieldsIsMissing when the class does not declare it
	 */
	private static Class<?> getFieldsEnum(Class<?> configurationClass) {
		Class<?>[] declaredClasses = configurationClass.getDeclaredClasses();
		for (Class<?> declaredClass : declaredClasses) {
			boolean isTheFieldsEnum = isTheFieldsEnum(declaredClass);
			if(isTheFieldsEnum) {
				return declaredClass;
			}
		}
		CcpErrorEntityConfigurationFieldsIsMissing fieldsEnumMissingError = new CcpErrorEntityConfigurationFieldsIsMissing(configurationClass);
		throw fieldsEnumMissingError;
	}

	/**
	 * Tells whether the nested type is the {@code Fields} enum: an enum named {@code Fields} that implements
	 * {@code CcpJsonFieldName}.
	 * @param declaredClass the nested type
	 * @return {@code true} for the {@code Fields} enum
	 */
	private static boolean isTheFieldsEnum(Class<?> declaredClass) {
		boolean isAnEnum = declaredClass.isEnum();
		boolean hasTheName = "Fields".equals(declaredClass.getSimpleName());
		boolean hasTheType = CcpJsonFieldName.class.isAssignableFrom(declaredClass);
		boolean isTheFieldsEnum = isAnEnum && hasTheName && hasTheType;
		return isTheFieldsEnum;
	}

	/**
	 * Chooses the transformer of a field: none without {@code @CcpEntityFieldsTransformer}; the class of
	 * {@code @CcpEntityFieldTransformer} on the field when present; otherwise the
	 * {@code CcpJsonTransformersDefaultEntityField} of the same name in the class of {@code @CcpEntityFieldsTransformer}
	 * (none when there is no such constant).
	 * @param name the field name
	 * @param field the field declaration
	 * @param configurationClass the configurator class
	 * @return the transformer
	 * @throws CcpEntityFieldCanNotBePrimaryKey when a primary key field uses a default transformer that cannot be primary key
	 */
	private static CcpBusiness getEntityFieldTransformer(String name, Field field, Class<?> configurationClass){
		boolean hasFieldsTransformer = configurationClass.isAnnotationPresent(CcpEntityFieldsTransformer.class);
	
		boolean isNotDecorated = false == hasFieldsTransformer;
		
		if(isNotDecorated) {
			return CcpOtherConstants.DO_NOTHING;
		}
		
		boolean hasCustomEntityFieldTransformer = field.isAnnotationPresent(CcpEntityFieldTransformer.class);
		if(hasCustomEntityFieldTransformer) {
			CcpEntityFieldTransformer annotation = field.getAnnotation(CcpEntityFieldTransformer.class);
			Class<?> transformerClass = annotation.value();
			CcpReflectionConstructorDecorator transformerConstructor = new CcpReflectionConstructorDecorator(transformerClass);
			CcpBusiness transformer = transformerConstructor.newInstance();
			return transformer;
		}
		
		CcpEntityFieldsTransformer annotation = configurationClass.getAnnotation(CcpEntityFieldsTransformer.class);
		Class<?> classReferenceWithTheFields = annotation.classReferenceWithTheFields();
		Field declaredField;
		CcpJsonTransformersDefaultEntityField defaultEntityField;
		try {
			declaredField = classReferenceWithTheFields.getDeclaredField(name);
			var fieldValue = declaredField.get(null);
			defaultEntityField = (CcpJsonTransformersDefaultEntityField)fieldValue;
		} catch (Exception e) {
			return CcpOtherConstants.DO_NOTHING;
		}
		boolean isPrimaryKeyField = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

		boolean isNotPrimaryKeyField = false == isPrimaryKeyField;

		 if(isNotPrimaryKeyField) {
			 return defaultEntityField;
		 }
		 
		 boolean canBePrimaryKey = defaultEntityField.canBePrimaryKey();
		
		 if(canBePrimaryKey) {
			 return defaultEntityField;
		 }
		 CcpEntityFieldCanNotBePrimaryKey cannotBePrimaryKeyError = new CcpEntityFieldCanNotBePrimaryKey(defaultEntityField);

		 throw cannotBePrimaryKeyError;
	}

	/** Raised when a primary key field uses a default transformer that changes the value in a way that cannot be part of an id. */
	@SuppressWarnings("serial")
	public static class CcpEntityFieldCanNotBePrimaryKey extends RuntimeException {
		/**
		 * Builds the error naming the transformer.
		 * @param defaultEntityField the transformer
		 */
		private CcpEntityFieldCanNotBePrimaryKey(CcpJsonTransformersDefaultEntityField defaultEntityField) {
			super("The field '" + defaultEntityField.name() + "' can not be a primary key");
		}
	}

	/** Raised when a configurator class does not declare the nested {@code Fields} enum. */
	@SuppressWarnings("serial")
	public static class CcpErrorEntityConfigurationFieldsIsMissing extends RuntimeException {
		/**
		 * Builds the error naming the configurator class.
		 * @param configurationClass the configurator class
		 */
		private CcpErrorEntityConfigurationFieldsIsMissing(Class<?> configurationClass) {
			super("The class '" + configurationClass.getName() + "' must declare a public static enum called 'Fields' implementing '" + CcpJsonFieldName.class.getSimpleName() + "'");
		}
	}

}
