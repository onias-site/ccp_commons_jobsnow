package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * After {@code delete} on the twin entity, sends an e-mail; a failure is recorded as a warning.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.AfterDeleteFromTwinEntitySendAnEmailMessageAndIfFailsSaveAWarning} (priority 5).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpAfterDeleteFromTwinEntitySendAnEmailMessageAndIfFailsSaveAWarning {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
