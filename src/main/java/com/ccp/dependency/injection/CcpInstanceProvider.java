package com.ccp.dependency.injection;

/**
 * Contrato para fábricas de dependência usadas pelo {@code CcpDependencyInjection}. Cada implementação
 * sabe como construir e entregar a instância concreta de uma determinada interface {@code T}.
 */
public interface CcpInstanceProvider<T> {
	/**
	 * Constrói e retorna a instância concreta do tipo {@code T}.
	 */
	T getInstance();
}
