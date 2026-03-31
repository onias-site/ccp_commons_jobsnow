package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public class CcpBulkHandlerDelete implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	private final CcpEntity entityToDelete;

	public CcpBulkHandlerDelete(CcpEntity entityToDelete) {
		this.entityToDelete = entityToDelete;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		CcpEntityDetails entityDetails = this.entityToDelete.getEntityDetails();
		List<CcpBulkItem> asList = entityDetails.getBulkItemsList(json, CcpBulkEntityOperationType.delete);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return this.entityToDelete;
	}
}
