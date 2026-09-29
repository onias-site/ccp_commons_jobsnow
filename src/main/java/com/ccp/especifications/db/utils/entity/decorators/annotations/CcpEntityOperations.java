package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Groups several {@code @CcpEntityOperation} configurations on an entity, and also defines
 * global exception handlers for every operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperations {

	/**
	 * Array of operations configured for the entity.
	 */
	CcpEntityOperation[] value();

	/**
	 * Global exception handlers applied to every operation.
	
	 */
	CcpExceptionFlow[] globalHandlers() default {};

}
