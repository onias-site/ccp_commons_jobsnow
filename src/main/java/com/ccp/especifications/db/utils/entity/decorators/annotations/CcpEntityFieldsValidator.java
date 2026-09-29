package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity to have its fields validated before write operations. The attribute points to the
 * class that holds the validation definitions of each field (the schema of valid fields).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityFieldsValidator {

	/**
	 * Class that defines the valid fields and their validation rules for this entity.
	
	 */
	@SuppressWarnings("rawtypes")
	Class classReferenceWithTheFields();

}
