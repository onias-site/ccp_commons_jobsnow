package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.decorators.engine.CcpEntityExpurgableOptions;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTimeType {
	CcpEntityExpurgableOptions type();
	int minValue() default Integer.MIN_VALUE;
	int maxValue() default Integer.MAX_VALUE;
}
