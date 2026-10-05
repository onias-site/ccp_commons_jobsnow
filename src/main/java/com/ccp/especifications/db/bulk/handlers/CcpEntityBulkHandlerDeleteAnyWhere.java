package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;

/**
 * Bulk handler that deletes a record from a twin entity and from its twin, whether the record was found in the
 * searched entity or not, so the record disappears wherever it is.
 */
public class CcpEntityBulkHandlerDeleteAnyWhere implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> {

	/** The entity searched (one side of the twin pair). */
	private final CcpEntity entity;

	/**
	 * Initializes the handler with the entity searched.
	 * @param entity one side of the twin pair
	 */
	public CcpEntityBulkHandlerDeleteAnyWhere(CcpEntity entity) {
		this.entity = entity;
	}

	/**
	 * Produces the delete items of both sides of the twin pair.
	 * @param json search parameters
	 * @param recordFound data of the record found
	 * @return the delete items
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		var deleteItems = this.deleteAll(json);
		return deleteItems;
	}

	/**
	 * Builds the {@code delete} items of the record in the entity (as a twin-decorated entity) and in its twin.
	 * @param json the record data
	 * @return the delete items of both entities
	 */
	protected ArrayList<CcpBulkItem> deleteAll(CcpJsonRepresentation json) {
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entityToSearch, CcpEntityDecoratorTypes.Twin);
		CcpEntity twinEntity = entityToSearch.getTwinEntity(CcpEntityDecoratorTypes.Twin);
		ArrayList<CcpBulkItem> result = new ArrayList<>();
		List<CcpBulkItem> customEntityDeleteItems = customEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
		result.addAll(customEntityDeleteItems);
		List<CcpBulkItem> twinEntityDeleteItems = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
		result.addAll(twinEntityDeleteItems);
		return result;
	
	}

	/**
	 * Produces the delete items of both sides of the twin pair, even though the record was not found in this side.
	 * @param json search parameters
	 * @return the delete items
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		var deleteItems = this.deleteAll(json);
		return deleteItems;
	}

	/**
	 * Returns the entity searched.
	 * @return the entity searched
	 */
	public CcpEntity getEntityToSearch() {
		return this.entity;
	}

}
