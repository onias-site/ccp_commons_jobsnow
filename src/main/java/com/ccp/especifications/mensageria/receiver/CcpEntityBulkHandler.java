package com.ccp.especifications.mensageria.receiver;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class CcpEntityBulkHandler implements CcpBusiness {

	private final Consumer<String[]> functionToDeleteKeysInTheCache;
	private final CcpExecuteBulkOperation executeBulkOperation;
	private final String operationFieldName;
	private final CcpEntity entity;
	
	public CcpEntityBulkHandler(CcpEntity entity, String operationFieldName, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		this.entity = entity;
		this.operationFieldName = operationFieldName;
		this.executeBulkOperation = executeBulkOperation;
		this.functionToDeleteKeysInTheCache = functionToDeleteKeysInTheCache;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		String operation = json.getDynamicVersion().getAsString(this.operationFieldName);
		CcpBulkHandlers valueOf = CcpBulkHandlers.valueOf(operation);
		CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> bulkHandler = valueOf.getBulkHandler(this.entity);
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, this.functionToDeleteKeysInTheCache, bulkHandler);
		return json;
	}
	

}
