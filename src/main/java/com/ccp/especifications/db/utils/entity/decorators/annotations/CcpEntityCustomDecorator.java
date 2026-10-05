package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** One custom decorator of an entity, declared inside {@code @CcpEntityCustomDecorators}. */
@Retention(RetentionPolicy.RUNTIME)
public @interface CcpEntityCustomDecorator {

	/**
	 * The decorator class, a subclass of {@code CcpCustomDecoratorEntity} with a no-arg constructor.
	 * @return the decorator class
	 */
	Class<?> value();
	/**
	 * The position in the decorator chain: higher priorities wrap the lower ones (see {@code CcpEntityDecoratorTypes}).
	 * @return the priority
	 */
	int priority();
}
