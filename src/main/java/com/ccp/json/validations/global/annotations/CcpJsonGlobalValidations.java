package com.ccp.json.validations.global.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define as validações globais do JSON de entrada para uma classe de validação: grupos de campos
 * onde ao menos um é obrigatório, grupos de todos-ou-nenhum e validadores customizados de classe.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface CcpJsonGlobalValidations {
	/** Grupos de campos onde ao menos um deve estar presente no JSON. */
	CcpJsonValidationFieldList[] requiresAtLeastOne() default {};
	/** Grupos de campos onde todos devem estar presentes ou nenhum. */
	CcpJsonValidationFieldList[] requiresAllOrNone() default {};
	/** Validadores customizados adicionais de nível de classe. */
	@SuppressWarnings("rawtypes")
	Class[] customJsonValidators() default {};
}
