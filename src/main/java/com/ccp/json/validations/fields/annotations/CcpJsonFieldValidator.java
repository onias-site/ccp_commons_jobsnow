package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.json.validations.fields.enums.CcpJsonFieldType;

@Target(FIELD)
@Retention(RUNTIME)
@SuppressWarnings("rawtypes")
public @interface CcpJsonFieldValidator {
	Class[] customFieldValidations() default {};
	Class[] validationsCatalog() default {};
	boolean required() default false;
	CcpJsonFieldType type();
}
