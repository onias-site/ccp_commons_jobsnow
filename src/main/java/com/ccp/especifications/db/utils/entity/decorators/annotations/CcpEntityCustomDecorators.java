package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the custom decorators of an entity, defined by the business modules (e.g. versionable, disposable,
 * asynchronous writer).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityCustomDecorators {

	/**
	 * The custom decorators.
	 * @return the custom decorators
	 */
	CcpEntityCustomDecorator[] value();
}
