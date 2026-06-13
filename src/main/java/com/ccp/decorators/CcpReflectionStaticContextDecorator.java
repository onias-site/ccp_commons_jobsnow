package com.ccp.decorators;

/**
 * Especialização de {@code CcpReflectionOptionsDecorator} para chamadas de métodos estáticos via reflexão.
 * Recebe um {@code CcpReflectionConstructorDecorator} e resolve a classe, sem criar instância.
 */
public class CcpReflectionStaticContextDecorator extends CcpReflectionOptionsDecorator {

	/**
	 * Resolve a classe pelo nome usando {@code constructor.forName()}.
	 */
	protected CcpReflectionStaticContextDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
	}
}
