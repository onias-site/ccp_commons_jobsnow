package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldNumberType {
	double[] allowedValues() default {};
	double minValue() default Integer.MIN_VALUE;
	double maxValue() default Integer.MAX_VALUE;

}
