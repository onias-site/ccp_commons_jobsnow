package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Declares, on an entity annotated with {@code @CcpEntityGrouperChild} or {@code @CcpEntityGrouperChildCounter}, how
 * the parents of one kind of group ({@link #parent}) are calculated from a record, when the relationship is
 * <b>oneToMany</b>: the record belongs to the parents its own values point to, so they are calculated again from the
 * stored record whenever needed and no field keeps their ids. A <b>manyToMany</b> relationship (a record in groups that
 * change with its contents, such as a resume in the group of each search it satisfies) declares its parents with a list
 * field annotated with {@code @CcpStoredParentIdsField} instead, which keeps the ids of the parents the record was
 * written with. Each parent is declared in exactly one of the two ways. Repeatable: one per parent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpEntityGrouperParentCombinationsList.class)
public @interface CcpEntityGrouperParentCombinations {

	/**
	 * The parent entity (a {@code CcpEntity}, given by its configurator or virtual entity class); it must be one of the
	 * classes of the {@code value} of {@code @CcpEntityGrouperChild} or of {@code @CcpEntityGrouperChildCounter}.
	 * @return the class of the parent entity
	 */
	Class<?> parent();

	/**
	 * Produces, from a record, the combination of values of each parent; created by reflection, with a
	 * constructor without parameters (private when the class exposes {@code INSTANCE}).
	 * @return the class that produces the combinations
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Function> value();
}
