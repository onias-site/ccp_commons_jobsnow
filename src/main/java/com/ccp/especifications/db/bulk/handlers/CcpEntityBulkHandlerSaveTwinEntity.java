package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Handler bulk especializado que salva (cria ou atualiza) um registro na "entidade wrapper"
 * (wrapped entity) de uma entidade twin, sem mover o registro da entidade original. Difere de
 * {@link CcpEntityBulkHandlerTransferRecordToTwinEntity} por não deletar o registro da entidade de origem.
 */
public class CcpEntityBulkHandlerSaveTwinEntity extends CcpEntityBulkHandlerTransferRecordToTwinEntity{

	/**
	 * Inicializa com a entidade cujo wrapper receberá o registro.
	 *
	 * @param entity entidade de origem (cujo wrapped receberá o registro)
	 */
	public CcpEntityBulkHandlerSaveTwinEntity(CcpEntity entity) {
		super(entity);
	}

	/**
	 * Gera itens de {@code update} na entidade wrapper (wrapped entity).
	 *
	 * @param json parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return lista de itens bulk de atualização na entidade wrapper
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
		
		var asList = this.getBulkItemsToSave(json, CcpBulkEntityOperationType.update);
		
		return asList;
	}

	/**
	 * Gera itens de {@code create} na entidade wrapper (wrapped entity).
	 *
	 * @param json parâmetros da busca
	 * @return lista de itens bulk de criação na entidade wrapper
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		var asList = getBulkItemsToSave(json, CcpBulkEntityOperationType.create);
		
		return asList;
	}

	private ArrayList<CcpBulkItem> getBulkItemsToSave(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity wrapedEntity = entityToSearch.getWrapedEntity();

		var itemTo = wrapedEntity.toBulkItems(json, operation);
		var asList = new ArrayList<CcpBulkItem>();
		
		asList.addAll(itemTo);
		return asList;
	}


}
