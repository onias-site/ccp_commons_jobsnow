package com.ccp.especifications.db.utils.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityTransferType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.db.utils.CcpErrorEntityIncorrectClassConfiguration;
import com.ccp.especifications.db.utils.CcpErrorEntityIncorrectClassConfiguration.IncorrectEntityClassConfigurationType;
import com.ccp.especifications.db.utils.CcpJsonTransformersDefaultEntityField;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityDecorators;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityExpurgable;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityFieldPrimaryKey;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityFieldTransformer;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntitySpecifications;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public class CcpEntityFactory {

	public final CcpEntityField[] entityFields;

	public final Class<?> configurationClass;

	public final CcpEntity entityInstance;

	public final boolean hasTwinEntity;
	
	
	public CcpEntityFactory(Class<?> configurationClass) {
		
		this.hasTwinEntity = configurationClass.isAnnotationPresent(CcpEntityTwin.class);
		this.entityFields = this.getFields(configurationClass);
		this.entityInstance = this.getTwinEntity(configurationClass);
		this.configurationClass = configurationClass;
	}
	
	private CcpEntity getTwinEntity(Class<?> configurationClass) {
		boolean isNotTwinEntity = configurationClass.isAnnotationPresent(CcpEntityTwin.class) == false;
		
		if(isNotTwinEntity) {
			CcpEntity entity = this.getEntityInstance(configurationClass);
			return entity;
		}
		CcpEntityTwin annotation = configurationClass.getAnnotation(CcpEntityTwin.class);
		String twinEntityName = annotation.twinEntityName();
		
		CcpEntity original = this.getEntityInstance(configurationClass);
		CcpEntity twin = this.getEntityInstance(configurationClass, twinEntityName, CcpEntityTransferType.Inactivate);
		
		DecoratorTwinEntity entity = new DecoratorTwinEntity(original, twin);
		return entity;
	}
	
	private CcpEntity getEntityInstance(Class<?> configurationClass) {
		
		String simpleName = configurationClass.getSimpleName();
		String snackCase = new CcpStringDecorator(simpleName).text().toSnakeCase().content;
		int indexOf = snackCase.indexOf("entity");
		String entityName = snackCase.substring(indexOf + 7);
	
		CcpEntity entity = this.getEntityInstance(configurationClass, entityName, CcpEntityTransferType.Reactivate);
		return entity;
	}

	private CcpEntity getEntityInstance(Class<?> configurationClass, String entityName, CcpEntityTransferType transferType) {

		CcpEntityBulkHandlerTransferRecordToReverseEntity entityTransferRecordToReverseEntity = new CcpEntityBulkHandlerTransferRecordToReverseEntity(transferType, configurationClass);
		CcpEntity entity = new DefaultImplementationEntity(entityName, configurationClass, entityTransferRecordToReverseEntity, this.entityFields);
		
		boolean hasDecorators = configurationClass.isAnnotationPresent(CcpEntityDecorators.class);
	
		if(hasDecorators) {
			CcpEntityDecorators annotation = configurationClass.getAnnotation(CcpEntityDecorators.class);
			Class<?>[] decorators = annotation.decorators();
			entity = getDecoratedEntity(entity, decorators);
		}		

		boolean isExpurgableEntity = configurationClass.isAnnotationPresent(CcpEntityExpurgable.class);
		
		boolean thisClassIsAnnotedByExpurgableAndDecoratorsAtSameTime = hasDecorators && isExpurgableEntity;
		
		if(thisClassIsAnnotedByExpurgableAndDecoratorsAtSameTime) {
			throw new CcpErrorEntityIncorrectClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.thisClassIsAnnotedByExpurgableAndDecoratorsAtSameTime);

		}
		int cacheExpires = CcpEntityExpurgableOptions.daily.cacheExpires;
		
		if(isExpurgableEntity) {
			CcpEntityExpurgable annotation = configurationClass.getAnnotation(CcpEntityExpurgable.class);
			
			Class<?>expurgableEntityFactory = annotation.expurgableEntityFactory();
			CcpEntityExpurgableOptions longevity = annotation.expurgTime();
			cacheExpires = longevity.cacheExpires;
			entity = getExpurgableEntity(expurgableEntityFactory, longevity, entity);
		}		

		CcpEntitySpecifications configuration = configurationClass.getAnnotation(CcpEntitySpecifications.class);
		boolean isCacheableEntity = configuration.cacheableEntity();
		
		if(isCacheableEntity) {
			entity = new DecoratorCacheEntity(entity, cacheExpires);
		}
		
		return entity;
	}

	private static CcpEntity getDecoratedEntity(CcpEntity entity, Class<?>... decorators) {
		try {
			for (Class<?> decorator : decorators) {
				CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(decorator);
				CcpEntityDecoratorFactory newInstance = reflection.newInstance();
				entity = newInstance.getEntity(entity);
			}
			return entity;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static CcpEntity getExpurgableEntity(Class<?> decorator, CcpEntityExpurgableOptions longevity, CcpEntity entity) {
		try {
			CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(decorator);
			CcpEntityExpurgableFactory newInstance = reflection.newInstance();
			CcpEntity expurgableEntity = newInstance.getEntity(entity, longevity);
			return expurgableEntity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CcpEntityField[] getFields(Class<?> configurationClass) {

		Class<?>[] declaredClasses = configurationClass.getDeclaredClasses();
		
		boolean didNotDeclareFieldsEnum = declaredClasses.length == 0;
		if(didNotDeclareFieldsEnum) {
			throw new CcpErrorEntityIncorrectClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.mustDeclarePublicStaticEnum);
		}
		
		CcpEntitySpecifications annotation = configurationClass.getAnnotation(CcpEntitySpecifications.class);
		Class<?> entitySchemeValidation = annotation.entityValidation();
		Field[] declaredFields = entitySchemeValidation.getDeclaredFields();
		List<CcpEntityField> list = new ArrayList<>();
		for (Field field : declaredFields) {
			
			Object object;
			try {
				object = field.get(null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			boolean skipThisField = false == object instanceof CcpJsonFieldName;
			
			if(skipThisField) {
				continue;
			}
			
			String name = ((CcpJsonFieldName)object).name();
			CcpBusiness transformer = this.getEntityFieldTransformer(name, field);
			boolean primaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);
			CcpEntityField entityField = new CcpEntityField(name, primaryKey, transformer);
			list.add(entityField);
		}
		
		CcpEntityField[] fields = list.toArray(new CcpEntityField[list.size()]);
		
		return fields;
	}
	
	private CcpBusiness getEntityFieldTransformer(String name, Field field){
		boolean hasCustomEntityFieldTransformer = field.isAnnotationPresent(CcpEntityFieldTransformer.class);
		if(hasCustomEntityFieldTransformer) {
			CcpEntityFieldTransformer annotation = field.getAnnotation(CcpEntityFieldTransformer.class);
			Class<?> value = annotation.value();
			CcpReflectionConstructorDecorator crcd = new CcpReflectionConstructorDecorator(value);
			CcpBusiness transformer = crcd.newInstance();
			return transformer;
		}
		
		CcpEntitySpecifications annotation = this.configurationClass.getAnnotation(CcpEntitySpecifications.class);
		Class<?> enumWithDefaultJsonTransformers = annotation.entityFieldsTransformers();
		Field declaredField;
		CcpJsonTransformersDefaultEntityField defaultEntityField;
		try {
			declaredField = enumWithDefaultJsonTransformers.getDeclaredField(name);
			defaultEntityField = (CcpJsonTransformersDefaultEntityField)declaredField.get(null);
		} catch (NoSuchFieldException e) {
			return CcpOtherConstants.DO_NOTHING;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		boolean primaryKey = field.isAnnotationPresent(CcpEntityFieldPrimaryKey.class);

		boolean isNotPrimaryKeyField = primaryKey == false;

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
