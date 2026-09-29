package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Bulk handler that implements reading without changes: if the record exists, produces items with the
 * {@code noop} operation (no effect); if it does not exist, applies a customizable function (default: empty
 * list). Useful to include existing records in a batch without changing them.
 */
public class CcpBulkHandlerRead implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entityToRead;

	private final Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch;

	/**
	 * Creates the handler with the default behavior when the record is not found: empty list.
	 *
	 * @param entityToRead entity whose records will be read
	 */
	public CcpBulkHandlerRead(CcpEntity entityToRead) {
		this(entityToRead, json -> new ArrayList<>());
	}
	
	

	/**
	 * Creates the handler with a custom behavior when the record is not found.
	 *
	 * @param entityToRead entity whose records will be read
	 * @param whenRecordWasNotFoundInTheEntitySearch function applied when the record is not found
	 */
	public CcpBulkHandlerRead(CcpEntity entityToRead, Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch) {
		this.entityToRead = entityToRead;
		this.whenRecordWasNotFoundInTheEntitySearch = whenRecordWasNotFoundInTheEntitySearch;
	}



	/**
	 * Produces bulk items with the {@code noop} operation, marking the record as "seen" without changing its state.
	 *
	 * @param json search parameters
	 * @param recordFound data of the record found
	 * @return list of bulk items with the noop operation
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> noopItems = this.entityToRead.toBulkItems(json, CcpBulkEntityOperationType.noop);
		return noopItems;
	}

	/**
	 * Applies the "not found" function given in the constructor to the matching bulk item.
	 *
	 * @param json search parameters
	 * @return result of the custom "not found" function
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		String recordId = this.entityToRead.calculateId(json);
		CcpBulkItem notFoundItem = new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entityToRead, recordId);
		List<CcpBulkItem> notFoundItems = this.whenRecordWasNotFoundInTheEntitySearch.apply(notFoundItem);
		return notFoundItems;
	}

	/**
	 * Returns the entity targeted by the reading.
	 *
	 * @return target entity
	 */
	public CcpEntity getEntityToSearch() {
		return this.entityToRead;
	}
}
