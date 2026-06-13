package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marca o campo como obrigatório na validação do JSON de entrada.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldValidatorRequired {
}
