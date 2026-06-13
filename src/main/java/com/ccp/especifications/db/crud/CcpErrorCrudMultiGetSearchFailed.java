package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
/**
 * Exceção lançada quando uma operação de multi-get no banco de dados retorna um erro explícito.
 * Extrai os campos {@code type} e {@code reason} do JSON de erro retornado pela API do banco
 * (Elasticsearch) e os formata na mensagem da exceção.
 */
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {
	enum JsonFieldNames implements CcpJsonFieldName{
		type, reason
	}

	/**
	 * Inicializa a exceção com mensagem no formato {@code "<type>. Reason: <reason>"} extraída do JSON de erro.
	 *
	 * @param error JSON de erro retornado pelo banco
	 */
	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(error.getAsString(JsonFieldNames.type) + ". Reason: " + error.getAsString(JsonFieldNames.reason));
	}
	
}
