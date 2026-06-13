package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Exceção lançada quando um {@link CcpBulkItem} específico não é localizado dentro de uma lista de
 * resultados bulk. A mensagem inclui o id, o nome da entidade e a lista completa de resultados
 * retornados, facilitando o diagnóstico.
 */
@SuppressWarnings("serial")
public class CcpErrorBulkItemNotFound extends RuntimeException {

	/**
	 * Inicializa a exceção com mensagem formatada contendo o id, a entidade e a lista completa de
	 * resultados onde o item deveria ter sido encontrado.
	 *
	 * @param bulkItem item que não foi localizado
	 * @param result lista completa de resultados retornados
	 */
	public CcpErrorBulkItemNotFound(CcpBulkItem bulkItem, List<CcpJsonRepresentation> result) {
		super( String.format("Id '%s' from entity '%s' not found. Complete list: " + result, bulkItem.id, bulkItem.entity.getEntityMetaData().entityName));
	}
	
}
