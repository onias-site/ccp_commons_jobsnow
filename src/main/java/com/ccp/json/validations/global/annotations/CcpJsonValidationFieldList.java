package com.ccp.json.validations.global.annotations;

/**
 * A group of field names used by {@code requiresAtLeastOne} and {@code requiresAllOrNone} of
 * {@code @CcpJsonGlobalValidations}: the names are the constants of the given enums.
 */
public @interface CcpJsonValidationFieldList {
	/**
	 * The enums whose constants name the fields of the group.
	 * @return the enum classes
	 */
	@SuppressWarnings("rawtypes")
	Class[] value() default{};
}
