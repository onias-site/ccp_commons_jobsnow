package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Keeps, in a field of the record, the moment of its insertion: the first save writes the current moment, the next saves
 * keep the stored one (whatever the input brings, even when the entity replaces the record on save), and the transfers
 * between the main and the twin side carry it along with the rest of the record. The implementation names the field.
 * <p>
 * Built by {@code CcpEntityDecoratorTypes.InsertionDate} (priority 7, inside {@code AsyncWriter}: it runs where the
 * write runs, and inside the counters of {@code @CcpEntityGrouperChildCounter}, which read the record as stored after
 * the write).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityInsertionDate {

	/**
	 * The decorator class that fills the field, a {@code CcpEntity} with a public constructor receiving the entity
	 * decorated so far.
	 * @return the decorator class
	 */
	Class<?> value();
}
