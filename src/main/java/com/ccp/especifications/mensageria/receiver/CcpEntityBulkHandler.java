package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class CcpEntityBulkHandler implements CcpBusiness {

	private final Consumer<String[]> functionToDeleteKeysInTheCache;
	private final CcpExecuteBulkOperation executeBulkOperation;
	private final CcpEntity entity;
	
	public CcpEntityBulkHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		this.entity = entity;
		this.executeBulkOperation = executeBulkOperation;
		this.functionToDeleteKeysInTheCache = functionToDeleteKeysInTheCache;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpEntityBulkHandlerTransferRecordToReverseEntity bulkHandler = this.entity.getTransferRecordToReverseEntity();
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, this.functionToDeleteKeysInTheCache, bulkHandler);
		return json;
	}
	

}
