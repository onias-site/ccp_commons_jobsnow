package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
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
 * The record may be a child of several kinds of group, one per class of {@link #value}. The parents of each kind are
 * the combinations produced from the record by a class declared for that parent, each one identified by the id the
 * parent class calculates for it, and the name of the parent entity is the kind of group. The parent is declared in one
 * of two ways:
 * <ul>
 * <li><b>oneToMany</b> ({@code @CcpEntityGrouperParentCombinations} on the entity, whose {@code parent} is that class):
 * the record belongs to the parents its own values point to, so the groups it must leave are calculated again from the
 * stored record and no field keeps their ids;</li>
 * <li><b>manyToMany</b> (a list field annotated with {@code @CcpStoredParentIdsField}, whose {@code parent} is that
 * class): the record keeps in that field the ids of the parents it was written with, which tells the groups it must
 * leave.</li>
 * </ul>
 * The annotation is repeatable: each declaration is a pair of decorators of its own, so a record may be a child kept by
 * its id in some kinds of group and a child kept as a JSON of some of its fields ({@link #childFields}) in others.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.GrouperChildBeforeWrite} (priority 7: it reads the stored record
 * before it changes) and {@code CcpEntityDecoratorTypes.GrouperChildAfterWrite} (priority 5: inside the field
 * transformers, after the write).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpEntityGrouperChildList.class)
public @interface CcpEntityGrouperChild {

	/**
	 * The classes of the parent entities (configurators or virtual entities, with the entity annotations), one per kind
	 * of group the record is a child of: the {@code calculateId} of each one gives the id of each parent from its
	 * combination, and its name is the kind of group. Each class is the {@code parent} of exactly one declaration: a
	 * {@code @CcpEntityGrouperParentCombinations} (oneToMany) or a field annotated with {@code @CcpStoredParentIdsField}
	 * (manyToMany).
	 * @return the classes of the parent entities
	 */
	Class<?>[] value();

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

	/**
	 * The field of the record kept as the child, for the decorators that keep a value of the record instead of its id
	 * (several records may share the value, as the recruiters of a domain). Empty, the default, for the other decorators.
	 * @return the name of the field
	 */
	String childField() default "";

	/**
	 * The fields of the record kept in the child, for the decorators that keep the id of the record: when not empty, the
	 * child is a JSON text with these fields of the record (as stored, after the field transformers) and the id of the
	 * record in {@code id}, which tells the versions of the same record apart. Empty, the default, the child is the id.
	 * @return the names of the fields
	 */
	String[] childFields() default {};

	/**
	 * Produce more fields of the child, with {@link #childFields}: each one a {@code Function} from the record (as
	 * {@link #childFields} reads it) to a JSON whose fields are merged into the child, before its {@code id}; created by
	 * reflection, with a constructor without parameters (private when the class exposes {@code INSTANCE}). Used when the
	 * child keeps a value calculated from the record (the names of some items of a list) instead of a field as stored.
	 * @return the producer classes
	 */
	Class<?>[] childFieldsProducers() default {};
}
