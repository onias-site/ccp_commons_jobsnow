package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica que as validações de um campo devem ser copiadas de um campo homônimo em outra classe,
 * evitando duplicação de regras de validação.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonCopyFieldValidationsFrom {
	/**
	 * Classe de origem das validações a copiar.
	 * @return classe de origem
	 */
	Class<?> value();
}
