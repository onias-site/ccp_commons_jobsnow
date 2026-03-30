package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpEntityBulkHandlerSaveTwinEntity extends CcpEntityBulkHandlerTransferRecordToReverseEntity{

	public CcpEntityBulkHandlerSaveTwinEntity(CcpEntityTransferType transferType, Class<?> entityClass) {
		super(transferType, entityClass);
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
		
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		var itemFrom = twinEntity.getBulkItemsList(json, CcpBulkEntityOperationType.delete);
		var itemTo = entityToSearch.getBulkItemsList(json, CcpBulkEntityOperationType.update);
		var asList = new ArrayList<CcpBulkItem>();
		asList.addAll(itemFrom);
		asList.addAll(itemTo);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		var itemToUpdate = entityToSearch.getBulkItemsList(json, CcpBulkEntityOperationType.create);
		return itemToUpdate;
	}

}
