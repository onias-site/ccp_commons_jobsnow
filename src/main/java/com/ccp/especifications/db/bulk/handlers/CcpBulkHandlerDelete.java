package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Handler bulk que implementa a lógica de exclusão: se o registro existe, gera itens de
 * {@code delete}; se não existe, aplica uma função customizável (padrão: lista vazia). Permite
 * tratar ausência do registro de forma configurável — ignorando silenciosamente ou lançando exceção.
 */
public class CcpBulkHandlerDelete implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entityToDelete;

	private final Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch;

	/**
	 * Cria o handler com comportamento padrão ao não encontrar o registro: lista vazia.
	 *
	 * @param entityToDelete entidade da qual o registro será excluído
	 */
	public CcpBulkHandlerDelete(CcpEntity entityToDelete) {
		this(entityToDelete, json -> new ArrayList<>());
	}
	
	

	/**
	 * Cria o handler com comportamento customizado ao não encontrar o registro.
	 *
	 * @param entityToDelete entidade da qual o registro será excluído
	 * @param whenRecordWasNotFoundInTheEntitySearch função aplicada quando o registro não é encontrado
	 */
	public CcpBulkHandlerDelete(CcpEntity entityToDelete, Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch) {
		this.entityToDelete = entityToDelete;
		this.whenRecordWasNotFoundInTheEntitySearch = whenRecordWasNotFoundInTheEntitySearch;
	}



	/**
	 * Gera itens bulk de {@code delete} para a entidade configurada.
	 *
	 * @param json parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return lista de itens bulk de exclusão
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> asList = this.entityToDelete.toBulkItems(json, CcpBulkEntityOperationType.delete);
		return asList;
	}

	/**
	 * Aplica a função de "não encontrado" fornecida no construtor ao item bulk correspondente.
	 *
	 * @param json parâmetros da busca
	 * @return resultado da função customizada de "não encontrado"
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		String calculateId = this.entityToDelete.calculateId(json);
		List<CcpBulkItem> apply = this.whenRecordWasNotFoundInTheEntitySearch.apply(new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entityToDelete, calculateId));
		return apply;
	}

	/**
	 * Retorna a entidade alvo de exclusão.
	 *
	 * @return entidade alvo
	 */
	public CcpEntity getEntityToSearch() {
		return this.entityToDelete;
	}
}
