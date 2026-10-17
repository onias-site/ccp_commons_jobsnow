package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Purges the history of a versionable entity ({@code @CcpEntityVersionable}) when its records are deleted everywhere.
 * Built by {@code CcpEntityDecoratorTypes.VersionablePurge} (priority 5: outside the twin, which resolves
 * {@code deleteAnyWhere} on its own and does not pass it inward, and inside the field transformers).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityVersionablePurge {

	/**
	 * The decorator class, a {@code CcpEntity} with a public constructor receiving the entity decorated so far.
	 * @return the decorator class
	 */
	Class<?> value();
}
