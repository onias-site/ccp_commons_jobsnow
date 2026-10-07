package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tells that the global validations of this class are the ones of another class, avoiding repeated
 * {@code @CcpJsonGlobalValidations}. Followed by the validation and by the rules explanation.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonCopyGlobalValidationsFrom {
	/**
	 * The class whose global validations are used.
	 * @return the source class
	 */
	Class<?> value();
}
