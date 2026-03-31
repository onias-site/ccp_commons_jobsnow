package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpHandleWithSearchResultsInTheEntity<T> {

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);
	
	CcpEntity getEntityToSearch();
	
}
