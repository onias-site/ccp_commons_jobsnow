package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumberNatural {
	long[] allowedValues() default {};
	long minValue() default Long.MIN_VALUE;
	long maxValue() default Long.MAX_VALUE;
	long exactValue() default Long.MIN_VALUE;

}
