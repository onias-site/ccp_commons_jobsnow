package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumber {
	double[] allowedValues() default {};
	boolean integerNumber() default false;
	double minValue() default Integer.MIN_VALUE;
	double maxValue() default Integer.MAX_VALUE;

}
