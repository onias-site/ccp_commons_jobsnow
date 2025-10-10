package com.ccp.json.validations.global.annotations;

public @interface CcpJsonValidationFieldList {
	
	String[] value() default{};
}
