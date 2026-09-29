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
	 * Produces {@code update} items; for immutable entities returns {@code noop}; for versionable
	 * entities, merges the new data over the existing data keeping only the updatable fields.
	 *
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
