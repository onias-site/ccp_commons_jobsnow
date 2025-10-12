package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;

public enum CcpMensageriaOperationType {

	entityBulkHandler {
		CcpBusiness getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			CcpBusiness result = new CcpEntityBulkHandler(entity, operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
			return result;
		}
	},
	entityCrud {
		CcpBusiness getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			CcpBusiness result = new EntityCrud(entity, operationFieldName);
			return result;
		}
	},
	none{
		CcpBusiness getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			throw new UnsupportedOperationException();
		}},
	;
	
	abstract CcpBusiness getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache);
	
}
