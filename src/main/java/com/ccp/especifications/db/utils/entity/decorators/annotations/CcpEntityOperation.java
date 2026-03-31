package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityStepType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperation {
	
	CcpExceptionFlow[] operationHandlers();
	
	CcpEntityType entityType();

	CcpEntityStepType when();

	@SuppressWarnings("rawtypes")
	Class[] execute();
	
}
