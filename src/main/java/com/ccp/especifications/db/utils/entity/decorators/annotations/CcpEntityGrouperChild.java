package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes the records of the entity children of groups: every write keeps the id of the record in the groups of its
 * parents, on the side of the grouper that matches the side of the entity (main or twin).
 * <ul>
 * <li>save: leaves the groups it was in, on either side, and enters the groups of its new parents on the side saved;</li>
 * <li>delete on the main side (deactivation): leaves the main groups and enters the twin groups;</li>
 * <li>delete on the twin side (reactivation): leaves the twin groups and enters the main groups;</li>
 * <li>deleteAnyWhere: leaves the groups of both sides.</li>
 * </ul>
 * The parents of a record are the combinations produced from it by the class of {@code @CcpStoredParentIdsField}, each
 * one identified by the id {@link #value} calculates for it, and the name of that entity is the kind of group; the
 * record keeps those ids in the list field annotated with {@code @CcpStoredParentIdsField}, which tells the groups it
 * must leave. Built by {@code CcpEntityDecoratorTypes.GrouperChildBeforeWrite} (priority 7: it reads the stored record
 * before it changes) and {@code CcpEntityDecoratorTypes.GrouperChildAfterWrite} (priority 5: inside the field
 * transformers, after the write).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityGrouperChild {

	/**
	 * The class of the entity (a configurator or a virtual entity, with the entity annotations) whose
	 * {@code calculateId} gives the id of each parent from its combination; its name is the kind of group.
	 * @return the class of the parent entity
	 */
	Class<?> value();

	/**
	 * The decorator class that runs the {@code before} side of the writes, a {@code CcpEntity} with a public
	 * constructor receiving the entity decorated so far and this annotation.
	 * @return the decorator class
	 */
	Class<?> beforeDecoratorClass();

	/**
	 * The decorator class that runs the {@code after} side of the writes, a {@code CcpEntity} with a public constructor
	 * receiving the entity decorated so far and this annotation.
	 * @return the decorator class
	 */
	Class<?> afterDecoratorClass();
}
