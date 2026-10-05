package com.ccp.especifications.db.utils.entity.fields.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associates a custom transformer with an entity field, overriding the default transformer of
 * {@code @CcpEntityFieldsTransformer}. The class must implement {@code CcpBusiness} and have a no-arg constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpEntityFieldTransformer {
	/**
	 * The transformer class of the field.
	 * @return the transformer class
	 */
	Class<?> value();
}
