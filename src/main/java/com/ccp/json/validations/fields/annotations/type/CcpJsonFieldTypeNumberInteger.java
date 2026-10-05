package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Constraints of integer fields: minimum, maximum, exact and allowed values. */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumberInteger {
	/**
	 * The values allowed for the field.
	 * @return the allowed values (empty means any)
	 */
	long[] allowedValues() default {};
	/**
	 * Minimum value allowed.
	 * @return the minimum value ({@code Long.MIN_VALUE} means no constraint)
	 */
	long minValue() default Long.MIN_VALUE;
	/**
	 * Maximum value allowed.
	 * @return the maximum value ({@code Long.MAX_VALUE} means no constraint)
	 */
	long maxValue() default Long.MAX_VALUE;
	/**
	 * Mandatory exact value.
	 * @return the exact value ({@code Long.MIN_VALUE} means no constraint)
	 */
	long exactValue() default Long.MIN_VALUE;

}
