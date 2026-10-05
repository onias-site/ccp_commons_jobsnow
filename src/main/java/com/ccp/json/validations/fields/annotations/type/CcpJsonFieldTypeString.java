package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Constraints of text fields: minimum, maximum and exact length, allowed values, regular expression, empty text and
 * java class names.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldTypeString {
	/**
	 * Mandatory exact length.
	 * @return the exact length ({@code Integer.MIN_VALUE} means no constraint)
	 */
	int exactLength() default Integer.MIN_VALUE;
	/**
	 * Minimum length.
	 * @return the minimum length ({@code Integer.MIN_VALUE} means no constraint)
	 */
	int minLength() default Integer.MIN_VALUE;
	/**
	 * Maximum length.
	 * @return the maximum length ({@code Integer.MAX_VALUE} means no constraint)
	 */
	int maxLength() default Integer.MAX_VALUE;
	/**
	 * Whether the empty text is accepted.
	 * @return {@code false} (the default) when the empty text is refused
	 */
	boolean allowsEmptyString() default false;

	/**
	 * Regular expression the text must match.
	 * @return the regular expression (empty means no constraint)
	 */
	String regexValidation() default "";
	
	/**
	 * Enums whose constant names are the only values allowed; empty means any value.
	 * @return the enums of allowed values
	 */
	@SuppressWarnings("rawtypes")
	Class[] allowedValuesEnum() default {};

	/**
	 * Whether the field carries the fully qualified name of a java class instead of free text. When {@code true}, the value
	 * is accepted only if the class loader of the application finds a class with that name.
	 * @return {@code false} by default
	 */
	boolean isJavaClass() default false;
}
