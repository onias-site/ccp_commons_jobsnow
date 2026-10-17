package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before {@code deleteAnyWhere} on the main entity, sends an e-mail; a failure is only logged.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeDeleteAnyWhereFromMainEntitySendAnEmailMessageAndIfFailsLogTheError} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpBeforeDeleteAnyWhereFromMainEntitySendAnEmailMessageAndIfFailsLogTheError {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
