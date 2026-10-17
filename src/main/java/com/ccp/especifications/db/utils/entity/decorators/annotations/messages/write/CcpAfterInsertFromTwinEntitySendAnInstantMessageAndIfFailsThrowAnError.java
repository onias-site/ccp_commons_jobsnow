package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * After {@code insert} on the twin entity, sends an instant message; a failure throws the error.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.AfterInsertFromTwinEntitySendAnInstantMessageAndIfFailsThrowAnError} (priority 5).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpAfterInsertFromTwinEntitySendAnInstantMessageAndIfFailsThrowAnError {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
