package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before {@code delete} on the twin entity, sends an e-mail and an instant message; a failure is only logged.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeDeleteFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsLogTheError} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpBeforeDeleteFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsLogTheError {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
