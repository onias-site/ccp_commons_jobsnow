package com.ccp.especifications.db.utils.entity.decorators.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface CcpExceptionFlow {
	Class<?>[] thenExecute();
	Class<?> whenThrowing();
}
