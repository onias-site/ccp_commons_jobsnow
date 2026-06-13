package com.ccp.dependency.injection;

/**
 * Exceção lançada quando {@code CcpDependencyInjection.getDependency()} não encontra nenhuma implementação
 * registrada para a interface solicitada. Indica erro de configuração do container de DI.
 */
@SuppressWarnings("serial")
public class CcpErrorDependencyInjectionMissing extends RuntimeException{

	/**
	 * Informa o nome da interface para a qual não há implementação registrada.
	 */
	public CcpErrorDependencyInjectionMissing(Class<?> interfaceClass) {
		super("It is missing an implementation of the interface " + interfaceClass.getName());
	}
}
