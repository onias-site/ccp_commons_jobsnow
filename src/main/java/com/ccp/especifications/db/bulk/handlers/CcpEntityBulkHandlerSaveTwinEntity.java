package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpEntityBulkHandlerSaveTwinEntity extends CcpEntityBulkHandlerTransferRecordToTwinEntity{

	public CcpEntityBulkHandlerSaveTwinEntity(CcpEntity entity) {
		super(entity);
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
		
		var asList = this.getBulkItemsToSave(json, CcpBulkEntityOperationType.update);
		
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		var asList = getBulkItemsToSave(json, CcpBulkEntityOperationType.create);
		
		return asList;
	}

	private ArrayList<CcpBulkItem> getBulkItemsToSave(CcpJsonRepresentation json,
			CcpBulkEntityOperationType operation) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity wrapedEntity = entityToSearch.getWrapedEntity();

		var itemTo = wrapedEntity.toBulkItems(json, operation);
		var asList = new ArrayList<CcpBulkItem>();
		
		asList.addAll(itemTo);
		return asList;
	}


}
