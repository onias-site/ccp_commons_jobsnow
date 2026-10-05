package com.ccp.especifications.db.bulk.handlers;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import java.util.stream.Stream;

/**
 * Bulk handler that implements a smart "upsert" logic: if the record exists, produces
 * {@code update} items merging the new data with the existing data (respecting updatable fields and
 * version); if it does not exist, produces {@code create} items. Honors the immutability rules
 * configured in the entity ({@code isNotAnUpdatableEntity}).
 */
public class CcpBulkHandlerSave implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	/** The entity where the records are created or updated. */
	private final CcpEntity mainEntity;

	/**
	 * Initializes the handler with the entity targeted by the upsert.
	 *
	 * @param mainEntity entity in which the records will be created or updated
	 */
	public CcpBulkHandlerSave(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	/**
	 * Produces the {@code update} items of the record, each one adjusted by {@code toUpdateRecord}.
	 * @param searchParameter search parameters with the new data
	 * @param recordFound data of the record existing in the database
	 * @return list of update or noop bulk items
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		List<CcpBulkItem> updateItems = this.mainEntity
			.toBulkItems(searchParameter, CcpBulkEntityOperationType.update);
			Stream<CcpBulkItem> updateItemsStream = updateItems
			.stream();
			var mergedItemsStream = updateItemsStream.map(x -> this.toUpdateRecord(searchParameter, recordFound, x));

			var mergedUpdateItems = mergedItemsStream	
			.collect(Collectors.toList())
				;
		return mergedUpdateItems;
	}

	/**
	 * Adjusts an update item to the rules of the entity: an entity that is not updatable turns the item into
	 * {@code noop}; otherwise the new data is merged with the stored record, where only the updatable fields of the new
	 * data overwrite the stored values, and the result is reduced to the fields declared by the entity.
	 * @param searchParameter search parameters with the new data
	 * @param recordFound data of the stored record
	 * @param bulkItem the update item
	 * @return the adjusted item
	 */
	private CcpBulkItem toUpdateRecord(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound , CcpBulkItem bulkItem) {

		CcpEntityMetaData entityDetails = bulkItem.entity.getEntityMetaData();
		
		boolean isNotAnUpdatableEntity = entityDetails.isNotAnUpdatableEntity();
		
		if(isNotAnUpdatableEntity) {
			CcpBulkItem updatedBulkItem = new CcpBulkItem(bulkItem.json, CcpBulkEntityOperationType.noop, bulkItem.entity, bulkItem.id);
			return updatedBulkItem;
		}
		boolean doesNotCreateVersions = false == bulkItem.operation.createsVersionsToSameRecord;

		if(doesNotCreateVersions) {
			return bulkItem;
		}
		
		CcpJsonRepresentation updatablePiece = bulkItem.json.getJsonPiece(entityDetails.onlyUpdatableFields);
		CcpJsonRepresentation jsonMergedWithRecordFound = bulkItem.json.mergeWithAnotherJson(recordFound);
		CcpJsonRepresentation mergedJson = jsonMergedWithRecordFound.mergeWithAnotherJson(updatablePiece);
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(mergedJson);
		CcpBulkItem updatedBulkItem = new CcpBulkItem(onlyExistingFields, bulkItem.operation, bulkItem.entity, bulkItem.id);

		return updatedBulkItem;
	}

	/**
	 * Produces {@code create} items with the data of {@code searchParameter}.
	 *
	 * @param searchParameter search parameters used to create the record
	 * @return list of create bulk items
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> createItems = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return createItems;
	}

	/**
	 * Returns the target entity.
	 *
	 * @return target entity
	 */
	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
