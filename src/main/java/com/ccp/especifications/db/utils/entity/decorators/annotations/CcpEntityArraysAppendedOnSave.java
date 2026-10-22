package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes the save of the entity add the items of the lists annotated with {@code @CcpEntityFieldAppendedOnSave} to the
 * ones already stored, instead of replacing them; the other fields are replaced as in any save. The database itself
 * does it, atomically: two saves of the same record at the same moment both get their items in, which a read, change
 * and write in the application cannot guarantee (the second write would carry a list read before the first one). Built
 * by {@code CcpEntityDecoratorTypes.ArraysAppendedOnSave} (priority 0, the innermost: inside {@code Disposable}, which
 * gives the write the id of the period).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityArraysAppendedOnSave {

	/**
	 * The decorator class, a {@code CcpEntity} with a public constructor receiving the entity decorated so far.
	 * @return the decorator class
	 */
	Class<?> value();
}
