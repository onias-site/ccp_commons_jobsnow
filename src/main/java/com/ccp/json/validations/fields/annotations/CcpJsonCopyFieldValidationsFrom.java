package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tells that the validations of the field are copied from the field of the same name in another class, avoiding
 * duplicated validation rules.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonCopyFieldValidationsFrom {
	/**
	 * The class whose field of the same name holds the validations.
	 * @return the source class
	 */
	Class<?> value();
}
