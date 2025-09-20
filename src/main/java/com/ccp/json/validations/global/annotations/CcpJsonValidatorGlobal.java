package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonValidatorGlobal {
	
	CcpJsonValidatorRequiredAtLeastOne[] requiredAtLeastOne() default {};
	
	@SuppressWarnings("rawtypes")
	Class[] customJsonValidators() default {};
	
}
