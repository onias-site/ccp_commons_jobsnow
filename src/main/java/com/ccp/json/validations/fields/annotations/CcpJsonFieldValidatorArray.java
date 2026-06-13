package com.ccp.json.validations.fields.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marca o campo como array e define restrições de tamanho e unicidade dos itens.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldValidatorArray {

	/**
	 * Tamanho exato obrigatório do array.
	 * @return tamanho exato (padrão: sem restrição)
	 */
	int exactSize() default Integer.MIN_VALUE;

	/**
	 * Exige itens únicos no array.
	 * @return true se itens repetidos são proibidos (padrão: true)
	 */
	boolean nonRepeatedItems() default true;

	/**
	 * Tamanho mínimo do array.
	 * @return tamanho mínimo (padrão: sem restrição)
	 */
	int minSize() default Integer.MIN_VALUE;

	/**
	 * Tamanho máximo do array.
	 * @return tamanho máximo (padrão: sem restrição)
	 */
	int maxSize() default Integer.MAX_VALUE;
}
