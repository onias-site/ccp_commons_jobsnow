package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures the twin entity pattern, in which a record migrates between two indexes (main entity and
 * twin entity) according to its state. Defines the cache cleanup function, the bulk executor and the name
 * of the twin index.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@SuppressWarnings("rawtypes")
public @interface CcpEntityTwin {

	/**
	 * Class that implements the cache invalidation logic when records are moved between entities.
	 */
	Class functionToDeleteKeysInTheCacheClass ();

	/**
	 * Class that implements the bulk operations executor for this entity.
	 */
	Class bulkExecutorClass ();

	/**
	 * Name of the twin index (the alternative target/source index).
	
	 */
	String twinEntityName();

}
