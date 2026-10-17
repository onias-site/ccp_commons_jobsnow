package com.ccp.especifications.db.utils.entity.decorators.annotations.operations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;

/**
 * Runs businesses {@code before} the {@code delete} operation when the entity is the main entity.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeDeleteFromMainEntity} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpBeforeDeleteFromMainEntity {

	/**
	 * Business classes executed in the order they are declared, each one receiving the output of the previous one.
	 * @return the business classes
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

	/**
	 * Exception handlers of the businesses: a business that throws a handled exception runs its handlers and the operation
	 * is canceled, returning {@code false}.
	 * @return the exception handlers
	 */
	CcpExceptionFlow[] operationHandlers();
}
