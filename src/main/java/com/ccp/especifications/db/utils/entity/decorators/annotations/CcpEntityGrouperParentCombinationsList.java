package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Holds the {@code @CcpEntityGrouperParentCombinations} repeated on an entity, one per parent. Written by the compiler
 * when the annotation appears more than once; never written by hand.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityGrouperParentCombinationsList {

	/**
	 * The repeated annotations.
	 * @return the repeated annotations
	 */
	CcpEntityGrouperParentCombinations[] value();
}
