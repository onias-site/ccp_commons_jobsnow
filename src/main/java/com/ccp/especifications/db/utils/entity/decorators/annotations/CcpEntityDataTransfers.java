package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Groups several {@code @CcpEntityDataTransfer} configurations on an entity, and also defines
 * global exception handlers for every transfer.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfers {

	/**
	 * Array of transfers configured for the entity.
	 */
	CcpEntityDataTransfer[] value();

	/**
	 * Global exception handlers applied to every transfer.
	
	 */
	CcpExceptionFlow[] globalHandlers() default {};

}
