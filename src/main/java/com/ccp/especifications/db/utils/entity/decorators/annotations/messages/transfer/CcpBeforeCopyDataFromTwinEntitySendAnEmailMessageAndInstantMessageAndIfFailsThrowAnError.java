package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.transfer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before {@code copyDataTo} on the twin entity, sends an e-mail and an instant message; a failure throws the error.
 * Repeatable: one per target entity.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeCopyDataFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsThrowAnError} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpBeforeCopyDataFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsThrowAnErrorList.class)
public @interface CcpBeforeCopyDataFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsThrowAnError {

	/**
	 * The message sent, a {@code CcpEntityMessageTemplate} with a no-arg constructor.
	 * @return the message class
	 */
	Class<?> messageTemplate();

	/**
	 * Configurator class of the target entity: the side effect runs only for transfers to it.
	 * @return the configurator class of the target entity
	 */
	Class<?> targetEntity();
}
