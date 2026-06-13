package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Valida que um timestamp é posterior ao momento atual dentro de um intervalo configurável.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeTimeAfter {
	/** Valor exato de intervalo após o momento atual. */
	int exactValue() default Integer.MAX_VALUE;
	/** Tipo de intervalo temporal (dias, horas, etc.). */
	CcpEntityExpurgableOptions intervalType();
	/** Valor mínimo de intervalo após o momento atual. */
	int minValue() default Integer.MIN_VALUE;
	/** Valor máximo de intervalo após o momento atual. */
	int maxValue() default Integer.MAX_VALUE;
}
