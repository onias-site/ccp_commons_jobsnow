package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface CcpJsonFieldAtLeastOne {
	String[] fields();
}
