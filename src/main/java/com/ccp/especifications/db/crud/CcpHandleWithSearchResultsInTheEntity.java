package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Callback contract to handle the search results of an entity. Defines two
 * paths — record found and record not found — and identifies the entity targeted by the
 * search.
 *
 * @param <T> type of the result returned by the callbacks

 */
public interface CcpHandleWithSearchResultsInTheEntity<T> {

	/**
	 * Handles a record that was found.
	 * @param searchParameter the search parameters merged over the record found
	 * @param recordFound the record found
	 * @return the result of the handling
	 */
	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	/**
	 * Handles a record that was not found.
	 * @param searchParameter the search parameters
	 * @return the result of the handling
	 */
	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);

	/**
	 * Returns the entity where the record is searched.
	 * @return the entity searched
	 */
	CcpEntity getEntityToSearch();

	/**
	 * Joins the record found with the search parameters before {@link #whenRecordWasFoundInTheEntitySearch}: by default the
	 * new values are merged into the stored record, so a transfer carries the whole record. The save handlers override it to
	 * let the entity decide ({@code CcpEntity.getRecordToSave}): an entity marked with {@code @CcpEntityReplacedOnSave}
	 * keeps only the new values.
	 * @param recordFound the stored record
	 * @param searchParameter the search parameters, already handled by the entity
	 * @return the json given to {@code whenRecordWasFoundInTheEntitySearch}
	 */
	default CcpJsonRepresentation joinRecordFound(CcpJsonRepresentation recordFound, CcpJsonRepresentation searchParameter) {
		CcpJsonRepresentation recordMergedWithSearch = recordFound.mergeWithAnotherJson(searchParameter);
		return recordMergedWithSearch;
	}
}
