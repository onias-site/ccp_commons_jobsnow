package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;

import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;

public enum CcpMensageriaOperationType {

	entityBulkHandler {
		CcpTopic getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			CcpTopic result = new CcpEntityBulkHandler(entity, operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
			return result;
		}
	},
	entityCrud {
		CcpTopic getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			CcpTopic result = new EntityCrud(entity, operationFieldName);
			return result;
		}
	},
	none{
		CcpTopic getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			throw new UnsupportedOperationException();
		}},
	;
	
	abstract CcpTopic getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache);
	
}
