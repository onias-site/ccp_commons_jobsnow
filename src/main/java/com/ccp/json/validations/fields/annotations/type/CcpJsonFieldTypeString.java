package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Restrições para campos String: comprimento mínimo/máximo/exato, valores permitidos,
 * regex e permissão de string vazia.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldTypeString {
	/** Comprimento exato obrigatório. */
	int exactLength() default Integer.MIN_VALUE;
	/** Comprimento mínimo permitido. */
	int minLength() default Integer.MIN_VALUE;
	/** Comprimento máximo permitido. */
	int maxLength() default Integer.MAX_VALUE;
	/** Se string vazia é aceita (padrão: false). */
	boolean allowsEmptyString() default false;
	/** Valores de string permitidos. */
	String[] allowedValues() default {};
	/** Expressão regular de validação. */
	String regexValidation() default "";
	
	@SuppressWarnings("rawtypes")
	/** Valores de enums (concatenados) permitidos. */
	Class[] allowedValuesEnum() default {};

}
