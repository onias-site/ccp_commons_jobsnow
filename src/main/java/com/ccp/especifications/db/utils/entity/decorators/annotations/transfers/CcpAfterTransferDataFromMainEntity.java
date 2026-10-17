package com.ccp.especifications.db.utils.entity.decorators.annotations.transfers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;

/**
 * Runs businesses {@code after} the {@code transferDataTo} operation when the entity is the main entity.
 * Repeatable: one per target entity.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.AfterTransferDataFromMainEntity} (priority 5).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpAfterTransferDataFromMainEntityList.class)
public @interface CcpAfterTransferDataFromMainEntity {

	/**
	 * Configurator class of the target entity: the side effect runs only for transfers to it.
	 * @return the configurator class of the target entity
	 */
	Class<?> targetEntity();

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
	CcpExceptionFlow[] transferHandlers();
}
