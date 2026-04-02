package com.ccp.especifications.db.bulk.handlers;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public class CcpBulkHandlerSave implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;
	
	public CcpBulkHandlerSave(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		
		var asList = this.mainEntity
			.toBulkItems(searchParameter, CcpBulkEntityOperationType.update)
			.stream().map(x -> this.toUpdateRecord(searchParameter, recordFound, x))	
			.collect(Collectors.toList())
				;
		return asList;
	}

	private CcpBulkItem toUpdateRecord(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound , CcpBulkItem x) {

		CcpEntityDetails entityDetails = x.entity.getEntityDetails();
		
		boolean isNotAnUpdatableEntity = entityDetails.isNotAnUpdatableEntity();
		
		if(isNotAnUpdatableEntity) {
			CcpBulkItem updatedBulkItem = new CcpBulkItem(x.json, CcpBulkEntityOperationType.noop, x.entity, x.id);
			return updatedBulkItem;
		}
		
		
		List<String> onlyUpdatableFields = entityDetails.onlyUpdatableFields;
		CcpDynamicJsonRepresentation dynamicVersion = searchParameter.getDynamicVersion();
		CcpJsonRepresentation updatablePiece = dynamicVersion.getJsonPiece(onlyUpdatableFields);
		CcpJsonRepresentation mergeWithAnotherJson = recordFound.mergeWithAnotherJson(updatablePiece);
		CcpBulkItem updatedBulkItem = new CcpBulkItem(mergeWithAnotherJson, x.operation, x.entity, x.id);

		return updatedBulkItem;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> asList = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
