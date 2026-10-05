package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Declares a past timestamp (milliseconds) and bounds how long before the current time it may be, in the granularity
 * of {@code intervalType}.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeTimeBefore {
	/**
	 * Exact distance before the current time.
	 * @return the exact distance ({@code Integer.MAX_VALUE} means no constraint)
	 */
	int exactValue() default Integer.MAX_VALUE;
	/** Tipo de intervalo temporal (dias, horas, etc.). */
	CcpEntityExpurgableOptions intervalType();
	/**
	 * Minimum distance before the current time.
	 * @return the minimum distance ({@code Integer.MIN_VALUE} means no constraint)
	 */
	int minValue() default Integer.MIN_VALUE;
	/**
	 * Maximum distance before the current time.
	 * @return the maximum distance ({@code Integer.MAX_VALUE} means no constraint)
	 */
	int maxValue() default Integer.MAX_VALUE;
}
