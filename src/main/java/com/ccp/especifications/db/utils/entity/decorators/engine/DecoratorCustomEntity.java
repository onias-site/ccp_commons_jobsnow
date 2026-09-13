package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorator;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

class DecoratorCustomEntity implements CcpEntityDecoratorType{
	
	private final Class<?> configurationClass;
	private final CcpEntityCustomDecorator annotation;
	
	public DecoratorCustomEntity(CcpEntityCustomDecorator annotation, Class<?> configurationClass) {
		this.configurationClass = configurationClass;
		this.annotation = annotation;
	}

	
	public int getPriority() {
		int priority = this.annotation.priority();
		return priority;
	}

	public boolean isDecorated(Class<?> clazz) {
		// DOUBT: UM DIA USAREMOS ESTE METODO???
		return false;
	}

	public CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity) {
		Class<?> value = this.annotation.value();
		CcpReflectionConstructorDecorator constructor = new CcpReflectionConstructorDecorator(value);
		CcpEntityBuilder entityBuilder = constructor.newInstance();
		CcpEntity customEntity = entityBuilder.getEntity(this.configurationClass, decoratedEntity);
		return customEntity;
	}
	
	

}
