package com.ccp.especifications.db.utils.entity.decorators.annotations.messages.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before {@code delete} on the twin entity, sends an e-mail; a failure is recorded as a warning.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.BeforeDeleteFromTwinEntitySendAnEmailMessageAndIfFailsSaveAWarning} (priority 7).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpBeforeDeleteFromTwinEntitySendAnEmailMessageAndIfFailsSaveAWarning {

	/**
	 * The messages sent, in the order they are declared, each one a {@code CcpEntityMessageTemplate} with a no-arg
	 * constructor.
	 * @return the message classes
	 */
	Class<?>[] value();
}
