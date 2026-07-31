package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Handler bulk que implementa a lógica "criar apenas se não existir": se o registro já existe na
 * entidade, não gera nenhum item bulk (lista vazia); se não existe, gera itens de {@code create}.
 * Usado como bloco de construção no pipeline de bulk com {@link com.ccp.especifications.db.bulk.CcpExecuteBulkOperation}.
 */
public class CcpBulkHandlerCreate implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;

	/**
	 * Inicializa o handler com a entidade alvo.
	 *
	 * @param mainEntity entidade na qual os registros serão criados
	 */
	public CcpBulkHandlerCreate(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	/**
	 * Retorna lista vazia, pois o registro já existe e não deve ser recriado.
	 *
	 * @param searchParameter parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return lista vazia
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		List<CcpBulkItem> asList = new ArrayList<CcpBulkItem>();
		return asList;
	}

	/**
	 * Converte o {@code searchParameter} em itens bulk de criação.
	 *
	 * @param searchParameter parâmetros da busca usados para criar o registro
	 * @return lista de itens bulk de criação
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> asList = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return asList;
	}

	/**
	 * Retorna a entidade associada a este handler.
	 *
	 * @return entidade alvo
	 */
	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
