package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes the entity versionable: every write keeps the previous state of the record in a history entity. Built by
 * {@code CcpEntityDecoratorTypes.Versionable} (priority 2, the innermost after the disposable, because it rewrites the
 * bulk items), always together with {@code @CcpEntityVersionablePurge}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityVersionable {

	/**
	 * The decorator class, a {@code CcpEntity} with a public constructor receiving the entity decorated so far: it
	 * knows the history entity and how a history row is built.
	 * @return the decorator class
	 */
	Class<?> value();
}
