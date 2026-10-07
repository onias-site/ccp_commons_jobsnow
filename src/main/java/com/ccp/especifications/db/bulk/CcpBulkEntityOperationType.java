
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Enumerates the possible bulk operation types on a database entity ({@code create},
 * {@code update}, {@code delete}, {@code noop}) and encapsulates the automatic reprocessing logic
 * for conflict or record-not-found situations — for example, promoting {@code create}
 * to {@code update} on a conflict, and vice versa.
 */
public enum CcpBulkEntityOperationType implements CcpJsonFieldName{

	/** Create operation; on a conflict (record already exists), it is automatically converted into {@code update}. */
	create(1, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.CONFLICT.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))),
	/** Update operation ({@code createsVersionsToSameRecord = true}); when the record is not found, it is automatically converted into {@code create}. */
	update(2, true, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))),
	/**
	 * Delete operation; when the record is not found as an error, throws {@link CcpErrorBulkEntityRecordNotFound}. With
	 * Elasticsearch this handler is not reached: a bulk delete of a missing document answers 404 with
	 * {@code "result": "not_found"} and no {@code error} object, so it does not count as a failure and the delete of what
	 * does not exist is silent (on purpose).
	 */
	delete(3, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x ->
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})),
	/** Null/no-effect operation; used to mark records that exist but do not need to be changed. */
	noop(0, false, CcpOtherConstants.EMPTY_JSON),
	;
	/**
	 * Whether the operation merges the new data with the record already stored (only the updatable fields of the new
	 * data overwrite the stored ones) instead of replacing it; true only for {@code update}.
	 */
	public final boolean createsVersionsToSameRecord;
	/** Reprocessing handlers of the operation, keyed by the HTTP status that triggers them. */
	private final CcpJsonRepresentation handlers;
	/**
	 * Precedence when the same record (entity and id) appears more than once in a bulk: the highest priority wins
	 * ({@code delete} over {@code update} over {@code create}); items with priority 0 ({@code noop}) are discarded.
	 */
	public final int priority;

	/**
	 * Associates the operation with its priority, versioning behavior and reprocessing handlers.
	 * @param priority precedence in a bulk
	 * @param createsVersionsToSameRecord whether the new data is merged with the stored record
	 * @param handlers reprocessing handlers keyed by HTTP status
	 */
	private CcpBulkEntityOperationType(int priority, boolean createsVersionsToSameRecord, CcpJsonRepresentation handlers) {
		this.createsVersionsToSameRecord = createsVersionsToSameRecord;
		this.handlers = handlers;
		this.priority = priority;
	}
	/**
	 * Turns a {@code create} item into an {@code update} of the same record.
	 * @param originalItem the original item
	 * @return the update item
	 */
	private static CcpBulkItem replaceCreateToUpdate(CcpBulkItem originalItem) {
		CcpBulkItem updateItem = new CcpBulkItem(originalItem, CcpBulkEntityOperationType.update);
		return updateItem;
	}
	/**
	 * Turns an {@code update} item into a {@code create} of the same record.
	 * @param originalItem the original item
	 * @return the create item
	 */
	private static CcpBulkItem replaceUpdateToCreate(CcpBulkItem originalItem) {
		CcpBulkItem createItem = new CcpBulkItem(originalItem, CcpBulkEntityOperationType.create);
		return createItem;
	}
	
	/**
	 * Decides how to reprocess the item of a bulk result. When the operation has a handler for the returned status, the
	 * handler is applied to the original item (e.g. a conflicting {@code create} becomes an {@code update}). Otherwise a
	 * {@code create} item is built in {@code entityToReprocess} with the JSON produced by {@code reprocessJsonProducer}
	 * (typically the error record).
	 * @param reprocessJsonProducer produces the JSON to record when the status has no handler
	 * @param result the result of the original bulk operation
	 * @param entityToReprocess entity where the JSON is created when the status has no handler
	 * @return the reprocessed bulk item
	 */
	public CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonProducer, CcpBulkOperationResult result, CcpEntity entityToReprocess) {
		
		CcpJsonFieldName statusAsJsonFieldName = result.statusAsJsonFieldName();
		boolean containsAllFields = this.handlers.containsAllFields(statusAsJsonFieldName);
		boolean statusNotMapped = false == containsAllFields;
		
		if(statusNotMapped) {
			CcpJsonRepresentation json = reprocessJsonProducer.apply(result);
			String recordId = entityToReprocess.calculateId(json);
			CcpBulkItem createItem = new CcpBulkItem(json, CcpBulkEntityOperationType.create, entityToReprocess, recordId);
			return createItem;
		}
		
		Function<CcpBulkItem,CcpBulkItem> handler = this.handlers.getAsObject(statusAsJsonFieldName);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpBulkItem reprocessedItem = handler.apply(bulkItem);
		return reprocessedItem;
	}
}
