package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Specialized bulk handler that saves (creates or updates) a record in the "wrapper entity"
 * (wrapped entity) of a twin entity, without moving the record out of the original entity. Differs from
 * {@link CcpEntityBulkHandlerTransferRecordToTwinEntity} in that it does not delete the record from the source entity.
 */
public class CcpEntityBulkHandlerSaveTwinEntity extends CcpEntityBulkHandlerTransferRecordToTwinEntity{

	/**
	 * Initializes with the entity whose wrapper will receive the record.
	 *
	 * @param entity source entity (whose wrapped entity will receive the record)
	 */
	public CcpEntityBulkHandlerSaveTwinEntity(CcpEntity entity) {
		// this class overrides the "not found" case to create the record; the superclass function is never
		// called, and therefore it cannot be the one that throws NOT_FOUND
		super(entity, item -> new ArrayList<>());
	}

	/**
	 * Produces {@code update} items in the wrapper entity (wrapped entity).
	 *
	 * @param json search parameters
	 * @param recordFound data of the record found
	 * @return list of update bulk items in the wrapper entity
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
		
		var saveItems = this.getBulkItemsToSave(json, CcpBulkEntityOperationType.update);
		
		return saveItems;
	}

	/**
	 * Produces {@code create} items in the wrapper entity (wrapped entity).
	 *
	 * @param json search parameters
	 * @return list of create bulk items in the wrapper entity
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		var saveItems = getBulkItemsToSave(json, CcpBulkEntityOperationType.create);
		
		return saveItems;
	}

	private ArrayList<CcpBulkItem> getBulkItemsToSave(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity wrapedEntity = entityToSearch.getWrapedEntity();

		var wrappedEntityItems = wrapedEntity.toBulkItems(json, operation);
		var bulkItemsToSave = new ArrayList<CcpBulkItem>();
		
		bulkItemsToSave.addAll(wrappedEntityItems);
		return bulkItemsToSave;
	}


}
