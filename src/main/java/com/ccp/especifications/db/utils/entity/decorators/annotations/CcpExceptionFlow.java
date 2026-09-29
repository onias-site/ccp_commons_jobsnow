package com.ccp.especifications.db.utils.entity.decorators.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Defines an exception handling flow: when an exception of type {@code whenThrowing} is
 * thrown during the execution of a business, the businesses listed in {@code thenExecute} are
 * executed in sequence.
 */
@Retention(RUNTIME)
public @interface CcpExceptionFlow {
	/** Business classes to execute when the configured exception is caught. */
	Class<?>[] thenExecute();
	/** Exception type that triggers this handling flow. */
	Class<?> whenThrowing();
}
