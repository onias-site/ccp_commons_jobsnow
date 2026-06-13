package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Exceção lançada quando uma operação bulk retorna erros. Carrega a lista completa de registros
 * que falharam serializada como string na mensagem da exceção, facilitando o diagnóstico em logs.
 */
@SuppressWarnings("serial")
public class CcpBulkErrors extends RuntimeException{

	/**
	 * Inicializa a exceção com a representação textual da lista de registros que falharam na operação bulk.
	 *
	 * @param failedRecords lista de registros que falharam
	 */
	public CcpBulkErrors(List<CcpJsonRepresentation> failedRecords) {
		super(failedRecords.toString());
	}
	
}
