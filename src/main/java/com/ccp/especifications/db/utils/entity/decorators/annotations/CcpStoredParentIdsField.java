package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.util.function.Function;

/**
 * Marks the field of an entity annotated with {@code @CcpEntityGrouperChild} that keeps the ids of the parents of the
 * record of one kind of group ({@link #parent}), as they were when it was written: the groups of that kind it must
 * leave. The decorator of {@code @CcpEntityGrouperChild} fills it on every save, replacing whatever the input brought,
 * so the field needs no transformer of its own. The field must be a list ({@code @CcpJsonFieldValidatorArray}).
 * <p>
 * It declares a <b>manyToMany</b> relationship: the parents of the record change with its contents (a resume is in the
 * group of each search it satisfies), so only the stored ids tell the groups it must leave. A <b>oneToMany</b>
 * relationship, whose parents come from the values of the record itself, needs no field: it is declared with
 * {@code @CcpEntityGrouperParentCombinations} on the entity. Each class of the {@code value} of
 * {@code @CcpEntityGrouperChild} is the {@link #parent} of exactly one of the two declarations.
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

	/**
	 * The parent entity (a {@code CcpEntity}, given by its configurator or virtual entity class) whose groups this field
	 * keeps the ids of; it must be one of the classes of the {@code value} of {@code @CcpEntityGrouperChild}, which is
	 * the link between the two annotations.
	 * @return the class of the parent entity
	 */
	Class<?> parent();
}
