package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public class CcpEntityBulkHandlerTransferRecordToReverseEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	//FIXME TROCAR PELA ENTITY
	private final CcpEntity entity;

	public CcpEntityBulkHandlerTransferRecordToReverseEntity(CcpEntity entity) {
		this.entity = entity;
	}
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		CcpEntityDetails entityDetails = twinEntity.getEntityDetails();
		var itemTo = entityDetails.getBulkItemsList(json, CcpBulkEntityOperationType.create);
		CcpEntityDetails entityDetails2 = entityToSearch.getEntityDetails();
		var itemFrom = entityDetails2.getBulkItemsList(json, CcpBulkEntityOperationType.delete);
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
	//FIXME EM CASO DE VALORES SENDO TRANSFERIDOS DE UMA ENTIDADE PARA OUTRA, CURRICULO POR EXEMPLO???
	
}
