package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contract that represents the result of a single bulk operation. Allows inspecting
 * success/failure, getting error details and triggering the proper reprocessing of the item based
 * on the HTTP status returned by the database.
 */
public interface CcpBulkOperationResult {

	/**
	 * Returns a JSON with the details of the error that occurred in this item's bulk operation.
	 *
	 * @return JSON with the error details
	 */
	CcpJsonRepresentation getErrorDetails();

	/**
	 * Returns the original {@link CcpBulkItem} that gave rise to this operation.
	 *
	 * @return original bulk item
	 */
	CcpBulkItem getBulkItem();

	/**
	 * Tells whether the operation resulted in an error.
	 *
	 * @return {@code true} if there was an error
	 */
	boolean hasError();

	/**
	 * Returns the HTTP status code of the bulk operation (e.g. 200, 201, 404, 409).
	 *
	 * @return HTTP status code
	 */
	int status();

	/**
	 * Delegates to {@link CcpBulkEntityOperationType#getReprocess} to determine how
	 * to reprocess the item based on the returned status.
	 *
	 * @param reprocessJsonMapper function that produces the JSON to reprocess
	 * @param reprocessEntity target entity of the reprocessing
	 * @return reprocessed bulk item
	 */
	default CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper, CcpEntity reprocessEntity) {
		CcpBulkItem bulkItem = this.getBulkItem(); 
		CcpBulkItem reprocessedItem = bulkItem.operation.getReprocess(reprocessJsonMapper, this, reprocessEntity);
		return reprocessedItem;
	}
	
	/**
	 * Builds and returns the cache key corresponding to this result's {@link CcpBulkItem}.
	 *
	 * @return cache key of the item
	 */
	default String getCacheKey() {
		CcpBulkItem bulkItem = this.getBulkItem();
		CcpCacheDecorator cache = new CcpCacheDecorator(bulkItem);
		return cache.key;
	}
	
	/**
	 * Converts the numeric status code into a {@link CcpJsonFieldName}, allowing it to be used as
	 * the key to look up handlers in the reprocessing map.
	 *
	 * @return status converted into {@link CcpJsonFieldName}
	 */
	default CcpJsonFieldName statusAsJsonFieldName() {
		int status = this.status();
		CcpJsonFieldName statusFieldName = new CcpFieldName(status);
		return statusFieldName;
	}
}
