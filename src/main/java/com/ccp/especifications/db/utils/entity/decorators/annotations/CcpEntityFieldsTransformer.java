package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity to have its fields transformed before the operations. The attribute points to the class
 * that holds the transformation definitions of each field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityFieldsTransformer {

	/**
	 * Class that holds the default transformers for the fields of this entity.
	
	 */
	@SuppressWarnings("rawtypes")
	Class classReferenceWithTheFields();

}
