package com.ccp.decorators;

/**
 * Contrato base do padrão Decorator do framework. Todo wrapper de tipo específico implementa esta interface,
 * garantindo que o conteúdo encapsulado possa ser recuperado de forma tipada.
 */
public interface CcpDecorator<T> {

	/**
	 * Retorna o objeto interno encapsulado pelo decorator.
	 */
	T getContent();

}
