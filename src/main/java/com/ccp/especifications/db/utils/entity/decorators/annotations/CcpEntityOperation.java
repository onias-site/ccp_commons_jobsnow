package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationType;

/**
 * Configures an operation with side effects for an entity (save, delete, deleteAnyWhere). The
 * combination of execution moment, operation type and source entity comes encapsulated in a
 * single item of {@code CcpEntityOperationType}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperation {

	/**
	 * Combination of {@code operationPhase}, {@code operationType} and {@code entityPhase} of this
	 * operation.
	 */
	CcpEntityOperationType operationType();

	/**
	 * Exception handlers specific to this operation.
	 */
	CcpExceptionFlow[] operationHandlers();

	/**
	 * Business classes to execute during the operation.
	
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

}
