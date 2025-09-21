package com.ccp.json.validations.global.annotations;

public @interface CcpJsonValidatorFieldList {
	
	String[] value() default{};
}
