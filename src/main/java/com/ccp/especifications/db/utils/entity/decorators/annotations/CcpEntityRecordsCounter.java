package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Keeps how many records the entity has on its main side and, when it is a twin entity, on its twin side: after each
 * write that can change those amounts (the save, that inserts on the main side; the delete, that deactivates on the main
 * side and reactivates on the twin side; the delete everywhere) both sides are counted again and the amounts are stored.
 * Counting again, instead of adding and subtracting, keeps the amounts right whatever happened before.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.RecordsCounter} (priority 5, outside {@code Twin}, so it sees the record
 * already moved between the sides); with {@code @CcpEntityAsyncWriter} it runs where the write runs, in the consumer of
 * the queue.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityRecordsCounter {

	/**
	 * The decorator class that counts and stores the amounts, a {@code CcpEntity} with a public constructor receiving the
	 * entity decorated so far.
	 * @return the decorator class
	 */
	Class<?> value();
}
