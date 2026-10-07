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
	 * Minimum value allowed. The default, {@code -Double.MAX_VALUE}, means no constraint, so any value (zero and negatives
	 * included) can be configured. Until 2026-10-06 the default was {@code Double.MIN_VALUE}, the smallest POSITIVE double:
	 * a minimum of zero or below was taken as "not configured", and where the rule ran anyway zero and negatives were
	 * refused.
	 * @return the minimum value
	 */
	double minValue() default -Double.MAX_VALUE;
	/**
	 * Maximum value allowed.
	 * @return the maximum value ({@code Double.MAX_VALUE} means no constraint)
	 */
	double maxValue() default Double.MAX_VALUE;
	/**
	 * Mandatory exact value.
	 * @return the exact value ({@code NaN}, the default, means no constraint; until 2026-10-06 it was
	 *         {@code Double.MIN_VALUE}, so an exact value of zero or below could not be configured)
	 */
	double exactValue() default Double.NaN;

}
