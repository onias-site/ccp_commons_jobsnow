package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Keeps how many records the entity has now as the child of a group of the grouper: a single group per entity, whose
 * only child is the JSON text {@code {"total": n}}, on the main side of the grouper for the main side of the entity and,
 * when the entity is a twin entity, on the twin side of the grouper for its twin side (each side has a counter of its
 * own). Every write (the save, the delete that moves the record between the sides, the delete everywhere) reads by id
 * whether the record is on each side before and after it and adds the difference: an insertion adds 1 to the main
 * side, the deactivation moves 1 from the main to the twin side, the reactivation moves it back, and the delete
 * everywhere subtracts 1 from the side the record was on. A total that reaches 0 deletes its group.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.GrouperChildTotal} (priority 7, outside {@code Twin} and the field
 * transformers, so it reads the record by its key as it came); with {@code @CcpEntityAsyncWriter} it runs where the
 * write runs, in the consumer of the queue. The records written before the annotation (or by bulk operations, which
 * skip the decorators) are counted by a load script.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityGrouperChildTotal {

	/**
	 * The decorator class that counts the records, a {@code CcpEntity} with a public constructor receiving the entity
	 * decorated so far.
	 * @return the decorator class
	 */
	Class<?> value();
}
