package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Declares that the value of the field must be a boolean. */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeBoolean {
}
