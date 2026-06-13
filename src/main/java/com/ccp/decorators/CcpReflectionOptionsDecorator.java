package com.ccp.decorators;

/**
 * Classe base da hierarquia de reflexão do framework. Encapsula o objeto {@code Class<?>} alvo e implementa
 * {@code CcpDecorator<Class<?>>}. As subclasses ({@code CcpReflectionNewInstanceDecorator} e
 * {@code CcpReflectionStaticContextDecorator}) especializam se a chamada é por instância ou por contexto estático.
 */
public abstract class CcpReflectionOptionsDecorator implements CcpDecorator<Class<?>> {

	public final Class<?> content;

	/**
	 * Encapsula a classe.
	 */
	protected CcpReflectionOptionsDecorator(Class<?> clazz) {
		this.content = clazz;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna a classe encapsulada.
	 */
	public Class<?> getContent() {
		return this.content;
	}

}
