package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpEntityBulkHandlerTransferRecordToTwinEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	private final CcpEntity entity;

	public CcpEntityBulkHandlerTransferRecordToTwinEntity(CcpEntity entity) {
		this.entity = entity;
	}
	
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

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return this.entity;
	}
	//ATTENTION EM CASO DE VALORES SENDO TRANSFERIDOS DE UMA ENTIDADE PARA OUTRA, CURRICULO POR EXEMPLO, OS FIELDS QUE ESTIVEREM VINDO NO 'SEARCHPARAMETER' SERAO OS NOVOS VALORES 
	
}
