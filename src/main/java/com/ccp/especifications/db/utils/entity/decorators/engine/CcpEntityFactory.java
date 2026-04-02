package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityConfigurationFieldsIsMissing;
import com.ccp.especifications.db.utils.entity.fields.CcpJsonTransformersDefaultEntityField;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldNotUpdatable;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.especifications.db.utils.entity.fields.annotations.CcpEntityFieldTransformer;

public class CcpEntityFactory {

	private static Function<Class<?>, String> mainEntityNameProducer = clazz -> {
		String simpleName = clazz.getSimpleName();
		String snackCase = new CcpStringDecorator(simpleName).text().toSnakeCase().content;
		int indexOf = snackCase.indexOf("entity");
		String substring = snackCase.substring(indexOf + 7);
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

	public static CcpEntity getEntity(Class<?> configurationClass, Function<Class<?>, String> function) {
		
		CcpEntityDetails entityDetails = new CcpEntityDetails(configurationClass, function);
		
		CcpEntity result = new DefaultImplementationEntity(entityDetails);
		List<DecoratorEntityEnum> collect = Arrays.asList(DecoratorEntityEnum.values()).stream().filter(x -> x.isDecorated(configurationClass)).collect(Collectors.toList());
		collect.sort((a,b) -> a.priority - b.priority);
		
		for (DecoratorEntityEnum decorator : collect) {
			result = decorator.getEntity(configurationClass, result);
		}
		
		return result;
	}
	
	public static CcpEntityField[] getFields(Class<?> configurationClass) {
		
		boolean didNotDeclareFieldsEnum = didNotDeclareFieldsEnum(configurationClass);
		
		if(didNotDeclareFieldsEnum) {
			throw new CcpErrorEntityConfigurationFieldsIsMissing(configurationClass);
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
			boolean skipThisField = false == object instanceof CcpJsonFieldName;
			
			if(skipThisField) {
				continue;
			}
			
			String name = ((CcpJsonFieldName)object).name();
			
			boolean updatable = false == field.isAnnotationPresent(CcpEntityFieldNotUpdatable.class);
			CcpBusiness transformer = getEntityFieldTransformer(name, field, configurationClass);
			boolean primaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

			CcpEntityField entityField = new CcpEntityField(name, primaryKey, updatable, transformer);
			list.add(entityField);
		}
		
		CcpEntityField[] fields = list.toArray(new CcpEntityField[list.size()]);
		
		return fields;
	}
	
	private static  boolean didNotDeclareFieldsEnum(Class<?> configurationClass) {
		
		Class<?>[] declaredClasses = configurationClass.getDeclaredClasses();
		
		boolean hasNoInternalClasses = declaredClasses.length == 0;
		
		if(hasNoInternalClasses) {
			return true;
		}
		
		Class<?> firstClass = declaredClasses[0];
		
		boolean isNotAnEnum = false == firstClass.isEnum();
		
		if(isNotAnEnum) {
			return true;
		}
		
		String simpleName = firstClass.getSimpleName();
		boolean incorrectName = false == "Fields".equals(simpleName);
		
		if(incorrectName) {
			return true;
			
		}
		
		boolean incorrectType = false == CcpJsonFieldName.class.isAssignableFrom(firstClass);
		if(incorrectType) {
			return true;
		}
		
		return false;
	}

	private static CcpBusiness getEntityFieldTransformer(String name, Field field, Class<?> configurationClass){
		
		boolean isNotDecorated = false == configurationClass.isAnnotationPresent(CcpEntityFieldsTransformer.class);
		
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
			defaultEntityField = (CcpJsonTransformersDefaultEntityField)declaredField.get(null);
		} catch (NoSuchFieldException e) {
			return CcpOtherConstants.DO_NOTHING;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		boolean isNotPrimaryKeyField = false == field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

		 if(isNotPrimaryKeyField) {
			 return defaultEntityField;
		 }
		 
		 boolean canBePrimaryKey = defaultEntityField.canBePrimaryKey();
		
		 if(canBePrimaryKey) {
			 return defaultEntityField;
		 }

		 throw new RuntimeException("The field '" + defaultEntityField.name() + "' can not be a primary key");
	}

}
