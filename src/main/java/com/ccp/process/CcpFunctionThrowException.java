package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.business.CcpBusiness;

/**
 * Implementação de {@code CcpBusiness} que sempre lança uma exceção pré-definida ao ser executada.
 * Útil em cenários de fluxo condicional onde uma determinada ramificação deve obrigatoriamente falhar.
 */
public class CcpFunctionThrowException implements CcpBusiness{

	private final RuntimeException exception;
	
	/**
	 * Armazena a exceção que será lançada.
	 * @param exception a exceção a ser lançada quando {@code apply} for chamado
	 */
	public CcpFunctionThrowException(RuntimeException exception) {
		this.exception = exception;
	}

	/** Lança imediatamente a exceção armazenada, sem processar o JSON. */
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		throw this.exception;
	}

}
