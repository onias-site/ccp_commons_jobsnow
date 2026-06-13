package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato que representa o resultado de uma operação bulk individual. Permite inspecionar
 * sucesso/falha, obter detalhes de erro e disparar o reprocessamento adequado do item baseado
 * no status HTTP retornado pelo banco.
 */
public interface CcpBulkOperationResult {

	/**
	 * Retorna um JSON com os detalhes do erro ocorrido na operação bulk deste item.
	 *
	 * @return JSON com detalhes do erro
	 */
	CcpJsonRepresentation getErrorDetails();

	/**
	 * Retorna o {@link CcpBulkItem} original que originou esta operação.
	 *
	 * @return item bulk original
	 */
	CcpBulkItem getBulkItem();

	/**
	 * Indica se a operação resultou em erro.
	 *
	 * @return {@code true} se houve erro
	 */
	boolean hasError();

	/**
	 * Retorna o código de status HTTP da operação bulk (ex.: 200, 201, 404, 409).
	 *
	 * @return código de status HTTP
	 */
	int status();

	/**
	 * Delega para {@link CcpBulkEntityOperationType#getReprocess} para determinar como
	 * reprocessar o item com base no status retornado.
	 *
	 * @param reprocessJsonMapper função que produz o JSON para reprocessamento
	 * @param reprocessEntity entidade destino do reprocessamento
	 * @return item bulk reprocessado
	 */
	default CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper, CcpEntity reprocessEntity) {
		CcpBulkItem bulkItem = this.getBulkItem(); 
		CcpBulkItem reprocess = bulkItem.operation.getReprocess(reprocessJsonMapper, this, reprocessEntity);
		return reprocess;
	}
	
	/**
	 * Gera e retorna a chave de cache correspondente ao {@link CcpBulkItem} deste resultado.
	 *
	 * @return chave de cache do item
	 */
	default String getCacheKey() {
		CcpBulkItem bulkItem = this.getBulkItem();
		CcpCacheDecorator cache = new CcpCacheDecorator(bulkItem);
		return cache.key;
	}
	
	/**
	 * Converte o código de status numérico em um {@link CcpJsonFieldName}, permitindo usá-lo como
	 * chave para lookup de handlers no mapa de reprocessamento.
	 *
	 * @return status convertido para {@link CcpJsonFieldName}
	 */
	default CcpJsonFieldName statusAsJsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.status());
		return ccpJsonFieldName;
	}
}
