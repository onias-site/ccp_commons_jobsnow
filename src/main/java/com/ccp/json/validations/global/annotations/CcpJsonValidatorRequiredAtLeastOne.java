package com.ccp.json.validations.global.annotations;

public @interface CcpJsonValidatorRequiredAtLeastOne {
	
	String[] oneOfThem() default{};
}
