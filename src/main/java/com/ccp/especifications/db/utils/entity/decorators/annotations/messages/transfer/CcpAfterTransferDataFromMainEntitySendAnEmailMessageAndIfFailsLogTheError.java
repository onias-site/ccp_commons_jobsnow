package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.transfer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * After {@code transferDataTo} on the main entity, sends an e-mail; a failure is only logged.
 * Repeatable: one per target entity.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.AfterTransferDataFromMainEntitySendAnEmailMessageAndIfFailsLogTheError} (priority 5).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpAfterTransferDataFromMainEntitySendAnEmailMessageAndIfFailsLogTheErrorList.class)
public @interface CcpAfterTransferDataFromMainEntitySendAnEmailMessageAndIfFailsLogTheError {

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
