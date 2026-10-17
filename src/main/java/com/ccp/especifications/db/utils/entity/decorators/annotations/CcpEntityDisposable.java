package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Makes the records of the entity expire: each record is valid only within the current period of the granularity.
 * Built by {@code CcpEntityDecoratorTypes.Disposable} (priority 1, the innermost).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDisposable {

	/**
	 * The decorator class, a {@code CcpEntity} with a public constructor receiving the entity decorated so far and the
	 * granularity.
	 * @return the decorator class
	 */
	Class<?> value();

	/**
	 * The expiration granularity.
	 * @return the granularity
	 */
	CcpEntityExpurgableOptions timeOption();
}
