package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes the writes of the entity asynchronous: they are published to the messaging system and run by its consumer.
 * Built by {@code CcpEntityDecoratorTypes.AsyncWriter} (priority 8: it cuts the chain and republishes the operation,
 * so it must receive the raw JSON, outside the field transformers, and stay inside the validator and the read-only
 * guard, which must refuse the write before it is published).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityAsyncWriter {

	/**
	 * The decorator class, a {@code CcpEntity} with a public constructor receiving the entity decorated so far (what the
	 * consumer runs).
	 * @return the decorator class
	 */
	Class<?> value();
}
