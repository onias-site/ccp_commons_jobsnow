package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityExpurgableOptions;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeTimeAfter {
	int exactValue() default Integer.MAX_VALUE;
	CcpEntityExpurgableOptions intervalType();
	int minValue() default Integer.MIN_VALUE;
	int maxValue() default Integer.MAX_VALUE;
}
