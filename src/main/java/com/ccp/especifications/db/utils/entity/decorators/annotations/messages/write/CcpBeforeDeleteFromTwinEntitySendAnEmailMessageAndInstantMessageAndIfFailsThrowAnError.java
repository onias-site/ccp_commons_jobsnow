package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before {@code delete} on the twin entity, sends an e-mail and an instant message; a failure throws the error.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeDeleteFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsThrowAnError} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpBeforeDeleteFromTwinEntitySendAnEmailMessageAndInstantMessageAndIfFailsThrowAnError {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
