package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato para acumulação e execução de operações bulk no banco de dados. Permite adicionar itens
 * individualmente ou em lote, limpar o buffer e disparar a execução, retornando os resultados de
 * cada operação.
 */
public interface CcpBulkExecutor {

	/**
	 * Remove todos os itens acumulados no buffer da operação bulk, reiniciando o executor.
	 *
	 * @return esta instância para encadeamento
	 */
	CcpBulkExecutor clearRecords();

	/**
	 * Converte o JSON e a entidade em {@link CcpBulkItem}s e adiciona cada um ao buffer.
	 *
	 * @param json dados do registro
	 * @param operation tipo de operação bulk
	 * @param entity entidade de destino
	 * @return esta instância para encadeamento
	 */
	default CcpBulkExecutor addRecord(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity) {
		
		List<CcpBulkItem> bulkItems =  entity.toBulkItems(json, operation);
		
		CcpBulkExecutor addRecord = this;
		
		for (CcpBulkItem bulkItem : bulkItems) {
			addRecord = this.addRecord(bulkItem);
		}
		
		return addRecord;
	}
	
	/**
	 * Adiciona um único {@link CcpBulkItem} já construído ao buffer do executor.
	 *
	 * @param bulkItem item a adicionar
	 * @return esta instância para encadeamento
	 */
	CcpBulkExecutor addRecord(CcpBulkItem bulkItem);

	/**
	 * Adiciona uma lista de {@link CcpBulkItem}s ao buffer, iterando e chamando {@link #addRecord} para cada um.
	 *
	 * @param items lista de itens a adicionar
	 * @return esta instância para encadeamento
	 */
	default CcpBulkExecutor addRecords(List<CcpBulkItem> items) {
		CcpBulkExecutor bulk = this;
		for (CcpBulkItem item : items) {
			bulk = bulk.addRecord(item);
		}
		return bulk;
	}
	
	/**
	 * Adiciona múltiplos registros JSON ao buffer convertendo cada um conforme a operação e entidade informadas.
	 *
	 * @param records lista de JSONs a adicionar
	 * @param operation tipo de operação bulk
	 * @param entity entidade de destino
	 * @return esta instância para encadeamento
	 */
	default CcpBulkExecutor addRecords(List<CcpJsonRepresentation> records, CcpBulkEntityOperationType operation, CcpEntity entity) {
		CcpBulkExecutor bulk = this;
		for (CcpJsonRepresentation _record : records) {
			bulk = bulk.addRecord(_record, operation, entity);
		}
		return bulk;
	}
	
	/**
	 * Executa as operações bulk acumuladas e retorna a lista de resultados de cada item processado.
	 *
	 * @return lista de resultados da operação bulk
	 */
	List<CcpBulkOperationResult> getBulkOperationResult();
	
}
