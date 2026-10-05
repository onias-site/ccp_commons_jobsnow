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
 * Bulk handler that implements the atomic transfer of a record from an entity to its
 * twin entity: if the record is found, produces a {@code create} in the twin entity and a
 * {@code delete} in the source entity; if it is not found, produces no operation. Used to
 * move records between mirrored entities (twin pattern).
 */
public class CcpEntityBulkHandlerTransferRecordToTwinEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	/** The source entity of the transfer. */
	private final CcpEntity entity;
	/** Decides what to do with the delete item of a record that was not found in the source entity. */
	private final Function<CcpBulkItem, List<CcpBulkItem>> handlerWhenNotFound;

	/**
	 * Initializes with the source entity of the transfer.
	 * @param entity source entity from which the record is transferred
	 * @param handlerWhenNotFound function applied to the delete item of a record that was not found
	 */
	public CcpEntityBulkHandlerTransferRecordToTwinEntity(CcpEntity entity, Function<CcpBulkItem, List<CcpBulkItem>> handlerWhenNotFound) {
		this.handlerWhenNotFound = handlerWhenNotFound;
		this.entity = entity; 
	}
	
	/**
	 * Produces a {@code create} item for the twin entity and a {@code delete} item for the source
	 * entity, performing the transfer in a single bulk operation.
	 *
	 * @param json search parameters
	 * @param recordFound data of the record found
	 * @return list with the create (twin) and delete (source) items
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		var twinCreateItems = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.create);
		var sourceDeleteItems = entityToSearch.toBulkItems(json, CcpBulkEntityOperationType.delete);
		var transferItems = new ArrayList<CcpBulkItem>();
		transferItems.addAll(twinCreateItems);
		transferItems.addAll(sourceDeleteItems);
		return transferItems;
	}

	/**
	 * Delegates the missing record to {@code handlerWhenNotFound}, through {@link CcpBulkHandlerDelete}.
	 * @param json search parameters
	 * @return the items produced by {@code handlerWhenNotFound}
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpBulkHandlerDelete handler = new CcpBulkHandlerDelete(entityToSearch, this.handlerWhenNotFound);
		List<CcpBulkItem> notFoundItems = handler.whenRecordWasNotFoundInTheEntitySearch(json);
		return notFoundItems;
	}

	/**
	 * Returns the source entity of the transfer.
	 *
	 * @return source entity
	 */
	public CcpEntity getEntityToSearch() {
		return this.entity;
	}
	//ATTENTION WHEN VALUES ARE TRANSFERRED FROM ONE ENTITY TO ANOTHER (A RESUME, FOR EXAMPLE), THE FIELDS COMING IN THE 'SEARCHPARAMETER' WILL BE THE NEW VALUES
	
}
