package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * A kind of entity decorator: whether it applies to a configurator class, its position in the chain and how to wrap an
 * entity with it.
 */
public interface CcpEntityDecoratorType {
	
	/**
	 * Returns the position of the decorator in the chain for the configurator class: higher priorities wrap the lower ones.
	 * @param configurationClass the configurator class
	 * @return the priority
	 */
	int getPriority(Class<?> configurationClass);
	
	/**
	 * Tells whether the decorator applies to the configurator class.
	 * @param clazz the configurator class
	 * @return {@code true} by default
	 */
	default boolean isAnnoted(Class<?> clazz) {
		return true;
	}
	/**
	 * Wraps the entity with this decorator.
	 * @param clazz the configurator class
	 * @param decoratedEntity the entity decorated so far
	 * @return the decorated entity
	 */
	CcpEntity getEntity(Class<?> clazz, CcpEntity decoratedEntity);

}
