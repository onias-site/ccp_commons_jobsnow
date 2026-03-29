package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;

class DecoratorTwinEntity extends CcpDefaultEntityDelegator<CcpEntityTwin>{
	
	final Class<?>  clazz;
	
	public DecoratorTwinEntity(CcpEntity2 entity, Class<?> clazz, Class<?> bulkExecutorClass, Class<?> functionToDeleteKeysInTheCacheClass) {
		super(entity, 5, instanciateBulkExecutor(bulkExecutorClass), instanciateFunctionToDeleteKeysInTheCache(functionToDeleteKeysInTheCacheClass));
		this.clazz = clazz;
	}
	
	private static Consumer<String[]> instanciateFunctionToDeleteKeysInTheCache(Class<?> functionToDeleteKeysInTheCacheClass) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(functionToDeleteKeysInTheCacheClass);
		Consumer<String[]> newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

	private static CcpExecuteBulkOperation instanciateBulkExecutor(Class<?> bulkExecutorClass) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(bulkExecutorClass);
		CcpExecuteBulkOperation newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

	public boolean isThisEntityDecorated(Class<CcpEntityTwin> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {
		return new ArrayList<>();
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		return result;
	}

	public CcpEntityTwin getAnnotation() {
		CcpEntityTwin annotation = this.clazz.getAnnotation(CcpEntityTwin.class);
		return annotation;
	}
}
