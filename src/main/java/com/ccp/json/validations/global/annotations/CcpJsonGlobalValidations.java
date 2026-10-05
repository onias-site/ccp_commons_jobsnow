package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares the global validations of the input JSON of a validation class: groups of fields where at least one is
 * required, all-or-none groups, and custom class-level validators.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonGlobalValidations {
	/**
	 * Groups of fields where at least one must be present in the JSON.
	 * @return the groups
	 */
	CcpJsonValidationFieldList[] requiresAtLeastOne() default {};
	/**
	 * Groups of fields where either all or none must be present.
	 * @return the groups
	 */
	CcpJsonValidationFieldList[] requiresAllOrNone() default {};
	/**
	 * Additional custom class-level validators (implementations of {@code CcpJsonValidator} with a no-arg constructor).
	 * @return the validator classes
	 */
	@SuppressWarnings("rawtypes")
	Class[] customJsonValidators() default {};
}
