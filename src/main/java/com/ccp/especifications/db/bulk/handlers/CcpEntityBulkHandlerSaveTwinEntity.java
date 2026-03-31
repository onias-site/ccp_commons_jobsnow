package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public class CcpEntityBulkHandlerSaveTwinEntity extends CcpEntityBulkHandlerTransferRecordToReverseEntity{

	public CcpEntityBulkHandlerSaveTwinEntity(CcpEntity entity) {
		super(entity);
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
		
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		CcpEntityDetails entityDetails = twinEntity.getEntityDetails();
		var itemFrom = entityDetails.getBulkItemsList(json, CcpBulkEntityOperationType.delete);
		CcpEntityDetails entityDetails2 = entityToSearch.getEntityDetails();
		var itemTo = entityDetails2.getBulkItemsList(json, CcpBulkEntityOperationType.update);
		var asList = new ArrayList<CcpBulkItem>();
		asList.addAll(itemFrom);
		asList.addAll(itemTo);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntityDetails entityDetails = entityToSearch.getEntityDetails();
		var itemToUpdate = entityDetails.getBulkItemsList(json, CcpBulkEntityOperationType.create);
		return itemToUpdate;
	}

}
