package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Permite indicar uma implementação customizada de {@code CcpJsonFieldType} para validar o campo.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldTypeCustom {
	/**
	 * Classe customizada que implementa {@code CcpJsonFieldType}.
	 * @return classe do validador customizado
	 */
	Class<?> value();
}
