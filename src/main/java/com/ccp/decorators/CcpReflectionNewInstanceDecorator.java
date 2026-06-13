package com.ccp.decorators;

/**
 * Especialização de {@code CcpReflectionOptionsDecorator} que mantém uma referência a uma instância de
 * objeto para uso em chamadas de métodos de instância via reflexão. Pode encapsular uma instância existente
 * ou criar uma nova via {@code CcpReflectionConstructorDecorator}.
 */
public class CcpReflectionNewInstanceDecorator extends CcpReflectionOptionsDecorator {

	public final Object instance;

	/**
	 * Encapsula uma instância já existente e a classe correspondente.
	 */
	public CcpReflectionNewInstanceDecorator(Object instance, Class<?> clazz) {
		super(clazz);
		this.instance = instance;
	}

	/**
	 * Carrega a classe e cria uma nova instância via reflexão.
	 */
	protected CcpReflectionNewInstanceDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
		this.instance = constructor.newInstance();
	}
}
