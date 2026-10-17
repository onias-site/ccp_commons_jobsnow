package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.util.function.Function;

/**
 * Marks the field of an entity annotated with {@code @CcpEntityGrouperChild} that keeps the ids of the parents of the
 * record, as they were when it was written: the groups it must leave. The decorator of {@code @CcpEntityGrouperChild}
 * fills it on every save, replacing whatever the input brought, so the field needs no transformer of its own. The field
 * must be a list ({@code @CcpJsonFieldValidatorArray}) and only one field of the entity carries this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpStoredParentIdsField {

	/**
	 * Produces, from a record, the combination of values of each parent; created by reflection, with a public
	 * constructor without parameters.
	 * @return the class that produces the combinations
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Function> value();
}
