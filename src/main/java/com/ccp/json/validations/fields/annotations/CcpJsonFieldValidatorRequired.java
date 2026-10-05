package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Marks the field as required in the validation of the input JSON (unless it declares a default value). */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldValidatorRequired {
}
