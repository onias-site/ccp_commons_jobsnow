package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public interface CcpHandleWithSearchResultsInTheEntity<T> {

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);
	
	CcpEntity getEntityToSearch();
	
	default List<CcpBusiness> doAfterSavingIfRecordIsNotFound(){
		return new ArrayList<>();
	}

	default List<CcpBusiness> doAfterSavingIfRecordIsFound(){
		return new ArrayList<>();
	}
	
}
