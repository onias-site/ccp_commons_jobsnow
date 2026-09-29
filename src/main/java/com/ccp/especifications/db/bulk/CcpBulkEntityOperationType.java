
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
	/** Delete operation; when the record is not found, throws {@link CcpErrorBulkEntityRecordNotFound}. */
	delete(3, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x ->
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})),
	/** Null/no-effect operation; used to mark records that exist but do not need to be changed. */
	noop(0, false, CcpOtherConstants.EMPTY_JSON),
	;
	public final boolean createsVersionsToSameRecord;
	private final CcpJsonRepresentation handlers;
	public final int priority;

	private CcpBulkEntityOperationType(int priority, boolean createsVersionsToSameRecord, CcpJsonRepresentation handlers) {
		this.createsVersionsToSameRecord = createsVersionsToSameRecord;
		this.handlers = handlers;
		this.priority = priority;
	}
	private static CcpBulkItem replaceCreateToUpdate(CcpBulkItem originalItem) {
		CcpBulkItem updateItem = new CcpBulkItem(originalItem, CcpBulkEntityOperationType.update);
		return updateItem;
	}
	private static CcpBulkItem replaceUpdateToCreate(CcpBulkItem originalItem) {
		CcpBulkItem createItem = new CcpBulkItem(originalItem, CcpBulkEntityOperationType.create);
		return createItem;
	}
	
	/**
	 * Evaluates the status returned by the bulk operation; if the status has no mapped handler, builds a new
	 * create {@link CcpBulkItem} through {@code reprocessJsonProducer}; otherwise, applies the matching
	 * handler (e.g. switches create to update) and returns the reprocessed item.
	 *
	 * @param reprocessJsonProducer function that produces the JSON to reprocess when the status is not mapped
	 * @param result result of the original bulk operation
	 * @param entityToReprocess target entity of the reprocessing
	 * @return reprocessed bulk item
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
