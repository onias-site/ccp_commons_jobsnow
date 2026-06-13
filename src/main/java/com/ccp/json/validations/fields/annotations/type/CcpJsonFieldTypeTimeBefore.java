package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Valida que um timestamp é anterior ao momento atual dentro de um intervalo configurável.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeTimeBefore {
	/** Valor exato de intervalo antes do momento atual. */
	int exactValue() default Integer.MAX_VALUE;
	/** Tipo de intervalo temporal (dias, horas, etc.). */
	CcpEntityExpurgableOptions intervalType();
	/** Valor mínimo de intervalo antes do momento atual. */
	int minValue() default Integer.MIN_VALUE;
	/** Valor máximo de intervalo antes do momento atual. */
	int maxValue() default Integer.MAX_VALUE;
}
