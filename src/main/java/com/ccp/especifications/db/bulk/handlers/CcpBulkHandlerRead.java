package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpBulkHandlerRead implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	private final CcpEntity entityToRead;
	
	private final Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch;

	public CcpBulkHandlerRead(CcpEntity entityToRead) {
		this(entityToRead, json -> new ArrayList<>());
	}
	
	

	public CcpBulkHandlerRead(CcpEntity entityToRead, Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch) {
		this.entityToRead = entityToRead;
		this.whenRecordWasNotFoundInTheEntitySearch = whenRecordWasNotFoundInTheEntitySearch;
	}



	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		List<CcpBulkItem> asList = this.entityToRead.toBulkItems(json, CcpBulkEntityOperationType.noop);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		String calculateId = this.entityToRead.calculateId(json);
		List<CcpBulkItem> apply = this.whenRecordWasNotFoundInTheEntitySearch.apply(new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entityToRead, calculateId));
		return apply;
	}

	public CcpEntity getEntityToSearch() {
		return this.entityToRead;
	}
}
