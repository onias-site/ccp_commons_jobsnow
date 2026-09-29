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

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);

	CcpEntity getEntityToSearch();
}
