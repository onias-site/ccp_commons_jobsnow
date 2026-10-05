package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Marks the field as a collection and sets the constraints on its size and on repeated items. */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldValidatorArray {

	/**
	 * Mandatory exact size of the collection.
	 * @return the exact size ({@code Integer.MIN_VALUE}, the default, means no constraint)
	 */
	int exactSize() default Integer.MIN_VALUE;

	/**
	 * Forbids repeated items.
	 * @return {@code true} (the default) when repeated items are forbidden
	 */
	boolean nonRepeatedItems() default true;

	/**
	 * Minimum size of the collection.
	 * @return the minimum size ({@code Integer.MIN_VALUE}, the default, means no constraint)
	 */
	int minSize() default Integer.MIN_VALUE;

	/**
	 * Maximum size of the collection.
	 * @return the maximum size ({@code Integer.MAX_VALUE}, the default, means no constraint)
	 */
	int maxSize() default Integer.MAX_VALUE;
}
