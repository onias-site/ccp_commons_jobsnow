package com.ccp.json.defaultvalues.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ccp.business.CcpBusiness;
import com.ccp.json.defaultvalues.business.CcpJsonFieldDefaultValueDoNothing;

/**
 * Define o valor padrão de um campo JSON. Funciona como as anotações de validação de campo, mas ao
 * invés de validar, insere valor no campo quando ele não foi informado no JSON em tratamento.
 * Campos já presentes no JSON nunca são sobrescritos.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface CcpJsonFieldDefaultValue {

	/**
	 * Valores padrão em texto. Cada string passa por {@code resolveTemplate}, de modo que
	 * {@code {nomeDoCampo}} é trocado pelo valor correspondente do JSON em tratamento.
	 * Um único item é gravado no campo como String; dois ou mais são gravados como lista de Strings.
	 * Quando vazio (padrão), o valor padrão passa a ser produzido por {@code jsonProducer}.
	 * @return os valores padrão do campo
	 */
	String[] defaultStrings() default {};

	/**
	 * {@code CcpBusiness} que produz o JSON com o campo já preenchido. Só é acionado quando
	 * {@code defaultStrings} está vazio. É instanciado por reflexão (construtor sem argumentos) e
	 * recebe, no método {@code execute}, o JSON em tratamento; o JSON devolvido substitui o original.
	 * O padrão devolve o JSON inalterado, isto é, não define valor padrão algum.
	 * @return a classe produtora do JSON
	 */
	Class<? extends CcpBusiness> jsonProducer() default CcpJsonFieldDefaultValueDoNothing.class;
}
