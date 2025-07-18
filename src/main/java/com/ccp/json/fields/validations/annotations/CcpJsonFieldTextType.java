package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTextType {
	String[] allowedValues() default {};
	int minLength() default 0;
	int maxLength() default 0;
	String regexValidation();

}
