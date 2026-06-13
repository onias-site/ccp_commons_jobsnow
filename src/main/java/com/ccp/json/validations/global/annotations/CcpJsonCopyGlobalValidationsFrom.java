package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica que as validações globais desta classe devem ser herdadas de outra classe, evitando
 * duplicação de anotações {@code @CcpJsonGlobalValidations} em classes de validação derivadas.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonCopyGlobalValidationsFrom {
	/** Classe de origem das validações globais a ser copiadas. */
	Class<?> value();
}
