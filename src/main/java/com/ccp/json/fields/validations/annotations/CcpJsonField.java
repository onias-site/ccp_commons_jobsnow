package com.ccp.json.fields.validations.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.json.fields.validations.enums.CcpJsonFieldRequiredOptions;
import com.ccp.json.fields.validations.enums.CcpJsonFieldTypes;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonField {

	CcpJsonFieldTypes type();
	CcpJsonFieldRequiredOptions required();
}
