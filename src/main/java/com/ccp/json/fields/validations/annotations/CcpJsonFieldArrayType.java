package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldArrayType {
	
	boolean nonRepeatedItems();
	int minSize() default Integer.MIN_VALUE;
	int maxSize() default Integer.MAX_VALUE;
}
