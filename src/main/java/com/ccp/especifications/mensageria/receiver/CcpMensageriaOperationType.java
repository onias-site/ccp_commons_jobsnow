package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;

public enum CcpMensageriaOperationType {

	entityBulkHandler {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			Function<CcpJsonRepresentation, CcpJsonRepresentation> result = new CcpEntityBulkHandler(entity, operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
			return result;
		}
	},
	entityCrud {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			Function<CcpJsonRepresentation, CcpJsonRepresentation> result = new EntityCrud(entity, operationFieldName);
			return result;
		}
	},
	none{
		CcpTopic getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			throw new UnsupportedOperationException();
		}},
	;
	
	abstract Function<CcpJsonRepresentation, CcpJsonRepresentation> getTopicType(CcpEntity entity, String operationFieldName,  CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache);
	
}
