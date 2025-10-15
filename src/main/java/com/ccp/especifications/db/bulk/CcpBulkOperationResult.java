package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpBulkOperationResult {

	CcpJsonRepresentation getErrorDetails();
	
	CcpBulkItem getBulkItem();
	
	boolean hasError();
	
	int status();
	
	default CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper, CcpEntity reprocessEntity) {
		CcpBulkItem bulkItem = this.getBulkItem(); 
		CcpBulkItem reprocess = bulkItem.operation.getReprocess(reprocessJsonMapper, this, reprocessEntity);
		return reprocess;
	}
	
	default String getCacheKey() {
		CcpBulkItem bulkItem = this.getBulkItem();
		CcpCacheDecorator cache = new CcpCacheDecorator(bulkItem);
		return cache.key;
	}
}
