package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Constraints of decimal fields: minimum, maximum, exact and allowed values. */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumber {
	/**
	 * The values allowed for the field.
	 * @return the allowed values (empty means any)
	 */
	double[] allowedValues() default {};
	/**
	 * Minimum value allowed. The default is {@code Double.MIN_VALUE}, the smallest POSITIVE double, not the most negative
	 * one.
	 * @return the minimum value
	 */
	double minValue() default Double.MIN_VALUE;
	/**
	 * Maximum value allowed.
	 * @return the maximum value ({@code Double.MAX_VALUE} means no constraint)
	 */
	double maxValue() default Double.MAX_VALUE;
	/**
	 * Mandatory exact value.
	 * @return the exact value ({@code Double.MIN_VALUE} means no constraint)
	 */
	double exactValue() default Double.MIN_VALUE;

}
