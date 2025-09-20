package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeText {
	String[] allowedValues() default {};
	int minLength() default Integer.MIN_VALUE;
	int maxLength() default Integer.MAX_VALUE;
	String regexValidation() default "";

}
