package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonGlobalValidations {
	
	CcpJsonValidationFieldList[] requiresAtLeastOne() default {};
	CcpJsonValidationFieldList[] requiresAllOrNone() default {};
	
	@SuppressWarnings("rawtypes")
	Class[] customJsonValidators() default {};
	
}
