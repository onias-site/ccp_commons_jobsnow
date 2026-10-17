package com.ccp.especifications.db.utils.entity.decorators.annotations.operations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;

/**
 * Runs businesses {@code after} the {@code save} operation when the entity is the twin entity.
 * Runs on every save; {@code After*Insert*} only when the save inserts and {@code After*Update*} only when it updates.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.AfterSaveFromTwinEntity} (priority 5).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpAfterSaveFromTwinEntity {

	/**
	 * Business classes executed in the order they are declared, each one receiving the output of the previous one.
	 * @return the business classes
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

	/**
	 * Exception handlers of the businesses: a business that throws a handled exception runs its handlers.
	 * @return the exception handlers
	 */
	CcpExceptionFlow[] operationHandlers();
}
