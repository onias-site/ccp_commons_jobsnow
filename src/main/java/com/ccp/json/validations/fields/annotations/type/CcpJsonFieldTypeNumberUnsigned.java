package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Restrições para campos inteiros long não-negativos (&gt;= 0).
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumberUnsigned {
	/** Valores permitidos para o campo. */
	long[] allowedValues() default {};
	/** Valor mínimo permitido. */
	long minValue() default Long.MIN_VALUE;
	/** Valor máximo permitido. */
	long maxValue() default Long.MAX_VALUE;
	/** Valor exato obrigatório. */
	long exactValue() default Long.MIN_VALUE;

}
