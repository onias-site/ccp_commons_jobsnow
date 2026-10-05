package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Declares that the value is a nested JSON, optionally validated by its own class and optionally forbidden to be empty. */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNestedJson {
	/**
	 * Validation class of the nested JSON (the default, this annotation itself, declares no rule).
	 * @return the validation class of the nested JSON
	 */
	Class<?> jsonValidation() default CcpJsonFieldTypeNestedJson.class;

	/**
	 * Whether an empty nested JSON is accepted.
	 * @return {@code true} (the default) when an empty JSON is accepted
	 */
	boolean allowsEmptyJson() default true;
}
