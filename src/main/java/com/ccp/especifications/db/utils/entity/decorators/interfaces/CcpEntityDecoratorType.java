package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpEntityDecoratorType {
	
	int getPriority(Class<?> configurationClass);
	
	default boolean isAnnoted(Class<?> clazz) {
		return true;
	}
	CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity);

}
