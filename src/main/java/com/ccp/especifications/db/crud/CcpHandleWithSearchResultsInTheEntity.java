package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.receiver.CcpTopic;

public interface CcpHandleWithSearchResultsInTheEntity<T> {

	T whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound);

	T whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter);
	
	CcpEntity getEntityToSearch();
	
	default List<CcpTopic> doAfterSavingIfRecordIsNotFound(){
		return new ArrayList<>();
	}

	default List<CcpTopic> doAfterSavingIfRecordIsFound(){
		return new ArrayList<>();
	}
	
}
