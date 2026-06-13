package com.ccp.service;

import com.ccp.decorators.CcpErrorJsonInvalid;

@SuppressWarnings("serial")
/**
 * Exceção lançada por {@code CcpService.execute()} quando ocorre uma {@code CcpErrorJsonInvalid} durante
 * o processamento do serviço, indicando que os dados processados resultaram em um JSON inválido.
 */
public class CcpServiceJsonValidationError extends RuntimeException {
	
	/** Encadeia a exceção original como causa. */
	public CcpServiceJsonValidationError(CcpErrorJsonInvalid e) {
		super(e);
	}
}
