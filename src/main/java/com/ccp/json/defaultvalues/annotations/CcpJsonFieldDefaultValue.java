package com.ccp.json.defaultvalues.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.business.CcpBusiness;
import com.ccp.json.defaultvalues.business.CcpJsonFieldDefaultValueDoNothing;

/**
 * Declares the default value of a JSON field. It works like the field validation annotations but, instead of
 * validating, it fills the field when the JSON being handled does not have it. Fields already present are never
 * overwritten. A field with a default value is never required.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldDefaultValue {

	/**
	 * Default values as text. Each one goes through {@code resolveTemplate}, so {@code {fieldName}} is replaced by the
	 * matching value of the JSON being handled. A single item is stored as a String; two or more are stored as a list of
	 * Strings. When empty (the default), the default value is produced by {@code jsonProducer}.
	 * @return the default values of the field
	 */
	String[] defaultStrings() default {};

	/**
	 * {@code CcpBusiness} that produces the JSON with the field filled. Used only when {@code defaultStrings} is empty. It is
	 * instantiated by reflection (no-arg constructor) and receives, in {@code execute}, the JSON being handled; the returned
	 * JSON replaces the original. The default returns the JSON unchanged, that is, it sets no default value.
	 * @return the producer class
	 */
	Class<? extends CcpBusiness> jsonProducer() default CcpJsonFieldDefaultValueDoNothing.class;
}
