package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Bulk handler that implements the "create only if it does not exist" logic: if the record already exists in the
 * entity, no bulk item is produced (empty list); if it does not exist, {@code create} items are produced.
 * Used as a building block in the bulk pipeline with {@link com.ccp.especifications.db.bulk.CcpExecuteBulkOperation}.
 */
public class CcpBulkHandlerCreate implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	/** The entity where the records are created. */
	private final CcpEntity mainEntity;

	/**
	 * Initializes the handler with the target entity.
	 *
	 * @param mainEntity entity in which the records will be created
	 */
	public CcpBulkHandlerCreate(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	/**
	 * Returns an empty list, because the record already exists and must not be created again.
	 *
	 * @param searchParameter search parameters
	 * @param recordFound data of the record found
	 * @return empty list
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		List<CcpBulkItem> noBulkItems = new ArrayList<CcpBulkItem>();
		return noBulkItems;
	}

	/**
	 * Converts the {@code searchParameter} into create bulk items.
	 *
	 * @param searchParameter search parameters used to create the record
	 * @return list of create bulk items
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> createItems = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return createItems;
	}

	/**
	 * Returns the entity associated with this handler.
	 *
	 * @return target entity
	 */
	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
