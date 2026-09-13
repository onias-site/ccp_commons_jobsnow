package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpEntityDecoratorType {
	
	int getPriority();
	boolean isDecorated(Class<?> clazz);
	CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity);

}
