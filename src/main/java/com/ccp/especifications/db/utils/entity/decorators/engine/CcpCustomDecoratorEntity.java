package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCustomDecorator;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

public abstract class CcpCustomDecoratorEntity implements CcpEntityDecoratorType{
	
	public final int getPriority(Class<?> configurationClass) {
		CcpEntityCustomDecorator annotation = configurationClass.getAnnotation(CcpEntityCustomDecorator.class);
		int priority = annotation.priority();
		return priority;
	}

	

}
