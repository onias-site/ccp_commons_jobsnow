package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.annotations.CcpEntityExceptionsFlow;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityBeforeSave {
	
	CcpEntityExceptionsFlow[] exceptionHandlers();
	@SuppressWarnings("rawtypes")
	Class[] flow();
}
