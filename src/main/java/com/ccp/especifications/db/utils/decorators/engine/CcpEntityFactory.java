package com.ccp.especifications.db.utils.decorators.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityDecorators;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityExpurgable;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntitySpecifications;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityTwin;
import com.ccp.exceptions.db.utils.CcpIncorrectEntityClassConfiguration;
import com.ccp.exceptions.db.utils.CcpIncorrectEntityClassConfiguration.IncorrectEntityClassConfigurationType;

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
		CcpEntity twin = this.getEntityInstance(configurationClass, twinEntityName, false);
		
		DecoratorTwinEntity entity = new DecoratorTwinEntity(original, twin);
		return entity;
	}
	
	private CcpEntity getEntityInstance(Class<?> configurationClass) {
		
		String simpleName = configurationClass.getSimpleName();
		String snackCase = new CcpStringDecorator(simpleName).text().toSnakeCase().content;
		int indexOf = snackCase.indexOf("entity");
		String entityName = snackCase.substring(indexOf + 7);
	
		CcpEntity entity = this.getEntityInstance(configurationClass, entityName, true);
		return entity;
	}

	private CcpEntity getEntityInstance(Class<?> configurationClass, String entityName, Boolean transferType) {

		CcpBulkHandlerTransferRecordToReverseEntity entityTransferRecordToReverseEntity = new CcpBulkHandlerTransferRecordToReverseEntity(transferType, configurationClass);
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
			throw new CcpIncorrectEntityClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.thisClassIsAnnotedByExpurgableAndDecoratorsAtSameTime);

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
				Constructor<?> declaredConstructor = decorator.getDeclaredConstructor();
				declaredConstructor.setAccessible(true);
				CcpEntityDecoratorFactory newInstance = (CcpEntityDecoratorFactory) declaredConstructor.newInstance();
				entity = newInstance.getEntity(entity);
			}
			return entity;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static CcpEntity getExpurgableEntity(Class<?> decorator, CcpEntityExpurgableOptions longevity, CcpEntity entity) {
		try {
			Constructor<?> declaredConstructor = decorator.getDeclaredConstructor();
			declaredConstructor.setAccessible(true);
			CcpEntityExpurgableFactory newInstance = (CcpEntityExpurgableFactory) declaredConstructor.newInstance();
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
			throw new CcpIncorrectEntityClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.mustDeclarePublicStaticEnum);
		}
		
		Class<?> firstClass = declaredClasses[0];
		
		boolean incorrectClassName = firstClass.getSimpleName().equals("Fields") == false;
		
		if(incorrectClassName) {
			throw new CcpIncorrectEntityClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.mustDeclareAnEnumCalledFields);
		}
		
		Method method;
		try {
			method = firstClass.getMethod("values");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		CcpEntityField[] fields;
		try {
			fields = (CcpEntityField[])method.invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		CcpEntityField field = fields[0];
		
		boolean doesNotImplementTheInterface = field instanceof CcpEntityField == false;
		
		if(doesNotImplementTheInterface) {
			throw new CcpIncorrectEntityClassConfiguration(configurationClass, IncorrectEntityClassConfigurationType.mustHaveAnEnumThatImplementsTheInterface);
		}
		
		return fields;
	}
	
	
}
