package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public interface CcpHandleWithSearchResultsInTheEntity<T> {

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);
	
	CcpEntity getEntityToSearch();
	
	default List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> doAfterSavingIfRecordIsNotFound(){
		return new ArrayList<>();
	}

	default List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> doAfterSavingIfRecordIsFound(){
		return new ArrayList<>();
	}
	
}
