package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica que o valor é um JSON aninhado, com opção de validação recursiva e controle de JSONs vazios.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNestedJson {
	/**
	 * Classe de validação do JSON interno (padrão: sem validação).
	 * @return classe de validação do JSON aninhado
	 */
	Class<?> jsonValidation() default CcpJsonFieldTypeNestedJson.class;

	/**
	 * Se JSON interno vazio é aceito.
	 * @return true se JSON vazio é permitido (padrão: true)
	 */
	boolean allowsEmptyJson() default true;
}
