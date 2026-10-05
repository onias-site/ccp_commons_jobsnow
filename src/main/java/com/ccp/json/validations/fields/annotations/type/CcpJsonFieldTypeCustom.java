package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Points to a custom {@code CcpJsonFieldType} implementation to validate the field. */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldTypeCustom {
	/**
	 * The custom class implementing {@code CcpJsonFieldType}.
	 * @return the custom validator class
	 */
	Class<?> value();
}
