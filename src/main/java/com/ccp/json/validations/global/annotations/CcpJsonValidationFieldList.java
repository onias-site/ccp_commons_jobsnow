package com.ccp.json.validations.global.annotations;

/**
 * Define uma lista de nomes de campos usada nos atributos {@code requiresAtLeastOne} e
 * {@code requiresAllOrNone} de {@code @CcpJsonGlobalValidations}.
 */
public @interface CcpJsonValidationFieldList {
	/** Nomes dos campos que compõem o grupo de validação. */
	String[] value() default{};
}
