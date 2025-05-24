package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class CcpBulkHandlerSaveTwin implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity supportEntity;
	
	public CcpBulkHandlerSaveTwin(CcpEntity supportEntity) {
		this.supportEntity = supportEntity;
	}
	

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		CcpEntity mainEntity = this.supportEntity.getTwinEntity();
		CcpBulkItem updateIntoSupportEntity = this.supportEntity.getMainBulkItem(searchParameter, CcpEntityBulkOperationType.update);
		CcpBulkItem deleteFromMainEntity = mainEntity.getMainBulkItem(searchParameter, CcpEntityBulkOperationType.delete);
		List<CcpBulkItem> asList = Arrays.asList(updateIntoSupportEntity, deleteFromMainEntity);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return this.supportEntity;
	}

}
