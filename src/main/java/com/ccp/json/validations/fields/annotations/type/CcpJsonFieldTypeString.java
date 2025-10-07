package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldTypeString {
	int exactLength() default Integer.MIN_VALUE;
	int minLength() default Integer.MIN_VALUE;
	int maxLength() default Integer.MAX_VALUE;
	boolean allowsEmptyString() default false;
	String[] allowedValues() default {};
	String regexValidation() default "";

}
