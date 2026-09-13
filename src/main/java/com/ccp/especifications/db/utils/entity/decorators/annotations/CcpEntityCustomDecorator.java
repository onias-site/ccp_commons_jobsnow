package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CcpEntityCustomDecorator {

	Class<?> value();
	int priority();
}
