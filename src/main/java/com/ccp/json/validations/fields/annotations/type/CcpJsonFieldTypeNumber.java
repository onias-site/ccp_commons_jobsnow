package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Restrições para campos numéricos double: valor mínimo, máximo, exato e lista de permitidos.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNumber {
	/** Valores permitidos para o campo. */
	double[] allowedValues() default {};
	/** Valor mínimo permitido. */
	double minValue() default Double.MIN_VALUE;
	/** Valor máximo permitido. */
	double maxValue() default Double.MAX_VALUE;
	/** Valor exato obrigatório. */
	double exactValue() default Double.MIN_VALUE;

}
