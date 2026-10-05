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
 * Bulk handler that implements the delete logic: if the record exists, produces
 * {@code delete} items; if it does not exist, applies a customizable function (default: empty list). Allows
 * handling the missing record in a configurable way — silently ignoring it or throwing an exception.
 */
public class CcpBulkHandlerDelete implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	/** The entity from which the records are deleted. */
	private final CcpEntity entityToDelete;

	/** Decides what to do with the (would-be) delete item of a record that was not found. */
	private final Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch;

	/**
	 * Creates the handler with the default behavior when the record is not found: empty list.
	 *
	 * @param entityToDelete entity from which the record will be deleted
	 */
	public CcpBulkHandlerDelete(CcpEntity entityToDelete) {
		this(entityToDelete, json -> new ArrayList<>());
	}
	
	

	/**
	 * Creates the handler with a custom behavior when the record is not found.
	 *
	 * @param entityToDelete entity from which the record will be deleted
	 * @param whenRecordWasNotFoundInTheEntitySearch function applied when the record is not found
	 */
	public CcpBulkHandlerDelete(CcpEntity entityToDelete, Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch) {
		this.entityToDelete = entityToDelete;
		this.whenRecordWasNotFoundInTheEntitySearch = whenRecordWasNotFoundInTheEntitySearch;
	}



	/**
	 * Produces {@code delete} bulk items for the configured entity.
	 *
	 * @param json search parameters
	 * @param recordFound data of the record found
	 * @return list of delete bulk items
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> deleteItems = this.entityToDelete.toBulkItems(json, CcpBulkEntityOperationType.delete);
		return deleteItems;
	}

	/**
	 * Applies the "not found" function given in the constructor to the matching bulk item.
	 *
	 * @param json search parameters
	 * @return result of the custom "not found" function
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		String recordId = this.entityToDelete.calculateId(json);
		CcpBulkItem deleteItem = new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entityToDelete, recordId);
		List<CcpBulkItem> notFoundItems = this.whenRecordWasNotFoundInTheEntitySearch.apply(deleteItem);
		return notFoundItems;
	}

	/**
	 * Returns the entity targeted by the deletion.
	 *
	 * @return target entity
	 */
	public CcpEntity getEntityToSearch() {
		return this.entityToDelete;
	}
}
