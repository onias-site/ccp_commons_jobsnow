package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Handler bulk que implementa a transferência atômica de um registro de uma entidade para sua
 * entidade twin: se o registro é encontrado, gera um {@code create} na entidade twin e um
 * {@code delete} na entidade de origem; se não é encontrado, não gera nenhuma operação. Usado para
 * mover registros entre entidades espelhadas (twin pattern).
 */
public class CcpEntityBulkHandlerTransferRecordToTwinEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entity;

	/**
	 * Inicializa com a entidade de origem da transferência.
	 *
	 * @param entity entidade de origem de onde o registro será transferido
	 */
	public CcpEntityBulkHandlerTransferRecordToTwinEntity(CcpEntity entity) {
		this.entity = entity;
	}
	
	/**
	 * Gera um item {@code create} para a entidade twin e um item {@code delete} para a entidade de
	 * origem, efetuando a transferência em uma única operação bulk.
	 *
	 * @param json parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return lista com itens de create (twin) e delete (origem)
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		var itemTo = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.create);
		var itemFrom = entityToSearch.toBulkItems(json, CcpBulkEntityOperationType.delete);
		var asList = new ArrayList<CcpBulkItem>();
		asList.addAll(itemTo);
		asList.addAll(itemFrom);
		return asList;
	}

	/**
	 * Retorna lista vazia, pois sem registro na origem não há o que transferir.
	 *
	 * @param json parâmetros da busca
	 * @return lista vazia
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		return new ArrayList<>();
	}

	/**
	 * Retorna a entidade de origem da transferência.
	 *
	 * @return entidade de origem
	 */
	public CcpEntity getEntityToSearch() {
		return this.entity;
	}
	//ATTENTION EM CASO DE VALORES SENDO TRANSFERIDOS DE UMA ENTIDADE PARA OUTRA, CURRICULO POR EXEMPLO, OS FIELDS QUE ESTIVEREM VINDO NO 'SEARCHPARAMETER' SERAO OS NOVOS VALORES 
	
}
