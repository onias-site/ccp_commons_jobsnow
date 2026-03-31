package com.ccp.especifications.db.bulk.handlers;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public class CcpBulkHandlerSave implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;
	
	public CcpBulkHandlerSave(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		CcpEntityDetails entityDetails = this.mainEntity.getEntityDetails();
		List<CcpBulkItem> asList = entityDetails.getBulkItemsList(recordFound, CcpBulkEntityOperationType.update);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		CcpEntityDetails entityDetails = this.mainEntity.getEntityDetails();
		List<CcpBulkItem> asList = entityDetails.getBulkItemsList(searchParameter, CcpBulkEntityOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
