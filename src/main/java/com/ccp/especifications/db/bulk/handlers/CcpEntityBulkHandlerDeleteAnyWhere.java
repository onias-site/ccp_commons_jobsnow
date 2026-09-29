package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;

public class CcpEntityBulkHandlerDeleteAnyWhere implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> {

	private final CcpEntity entity;

	public CcpEntityBulkHandlerDeleteAnyWhere(CcpEntity entity) {
		this.entity = entity;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		var deleteItems = this.deleteAll(json);
		return deleteItems;
	}

	protected ArrayList<CcpBulkItem> deleteAll(CcpJsonRepresentation json) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entityToSearch, CcpEntityDecoratorTypes.Twin);
		CcpEntity twinEntity = entityToSearch.getTwinEntity(CcpEntityDecoratorTypes.Twin);
		ArrayList<CcpBulkItem> result = new ArrayList<>();
		List<CcpBulkItem> customEntityDeleteItems = customEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
		result.addAll(customEntityDeleteItems);
		List<CcpBulkItem> twinEntityDeleteItems = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
		result.addAll(twinEntityDeleteItems);
		return result;
	
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		var deleteItems = this.deleteAll(json);
		return deleteItems;
	}

	public CcpEntity getEntityToSearch() {
		return this.entity;
	}

}
