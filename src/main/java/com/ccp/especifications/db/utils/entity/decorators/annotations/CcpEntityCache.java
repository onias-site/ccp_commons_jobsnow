package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity to have its records cached. The {@code value} attribute defines the cache
 * expiration time in seconds.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityCache {

	/**
	 * Cache expiration time in seconds.
	
	 */
	int value();
}
