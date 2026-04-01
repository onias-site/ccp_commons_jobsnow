package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpBulkHandlerCreate implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;
	
	public CcpBulkHandlerCreate(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		List<CcpBulkItem> asList = new ArrayList<CcpBulkItem>();
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> asList = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
