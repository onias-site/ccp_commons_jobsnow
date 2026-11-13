package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Makes the records of the entity counted in groups: the group of each parent keeps, as its only child, a single JSON
 * text with one counter per key ({@link #counterKeyClass}, the domain of the e-mail of a recruiter, for one), and
 * {@code childCount} is the number of keys (in the other groups it is the size of the {@code child} list). A key must
 * hold no quote. A record is counted once per parent: the save adds 1 to its key in the
 * groups of the parents it was not in at its previous write (all of them, on the insert), and the writes that keep the
 * record in the same parents count nothing. The deletes leave the counters as they are (they are a history).
 * <p>
 * The parents of each class of {@link #value} are declared on the entity as for {@code @CcpEntityGrouperChild}, most
 * often by {@code @CcpEntityGrouperParentCombinations} (oneToMany), and they are calculated from the record as it came
 * (before the field transformers), each primary key transformed as the entity of the parent ids does it. Built by
 * {@code CcpEntityDecoratorTypes.GrouperChildCounterBeforeWrite} (priority 7, inside {@code GrouperChildBeforeWrite}:
 * it reads the stored record before it changes, and the record already carries what the decorator of
 * {@code @CcpEntityGrouperChild} puts in it).
 * <p>
 * The class given to {@link #beforeDecoratorClass} decides how the record is counted: the history above
 * ({@code JnGrouperChildCounterEntityBeforeWrite}, one key per record), or the current state
 * ({@code JnGrouperChildCurrentCounterEntityBeforeWrite}, several keys per record, every write subtracting the keys the
 * record had on the main side and adding the ones it has there now, a key at 0 leaving the group).
 * <p>
 * The annotation is repeatable: each declaration is a counting decorator of its own, with its own parents, keys and
 * way of counting.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(CcpEntityGrouperChildCounterList.class)
public @interface CcpEntityGrouperChildCounter {

	/**
	 * The classes of the parent entities (configurators or virtual entities, with the entity annotations), one per kind
	 * of group the record is counted in: the {@code calculateId} of each one gives the id of each parent from its
	 * combination, and its name is the kind of group.
	 * @return the classes of the parent entities
	 */
	Class<?>[] value();

	/**
	 * Produces, from the record, the key of its counter (a {@code Function} from the record to a text), or its keys (to a
	 * {@code List<String>}) when the {@link #beforeDecoratorClass} counts several keys per record; created by reflection,
	 * with a constructor without parameters (private when the class exposes {@code INSTANCE}).
	 * @return the class that produces the key
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends Function> counterKeyClass();

	/**
	 * The decorator class that counts the record, a {@code CcpEntity} with a public constructor receiving the entity
	 * decorated so far and this annotation.
	 * @return the decorator class
	 */
	Class<?> beforeDecoratorClass();
}
