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
}
