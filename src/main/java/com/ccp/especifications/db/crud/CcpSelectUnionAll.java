package com.ccp.especifications.db.crud;

import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.CcpEntity;

public class CcpSelectUnionAll {

	public final CcpJsonRepresentation  condensed;

	public CcpSelectUnionAll(List<CcpJsonRepresentation> results) {
		CcpJsonRepresentation  condensed = CcpOtherConstants.EMPTY_JSON;
		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();
		
		for (CcpJsonRepresentation result : results) {
			String id = result.getAsString(fieldNameToId);
			String entity = result.getAsString(fieldNameToEntity);
			CcpJsonRepresentation removeKeys = result.removeFields(fieldNameToId, fieldNameToEntity);
			condensed = condensed.addToItem(entity, id, removeKeys);
		}
		this.condensed = condensed;
	}
	
	public boolean isPresent(String entityName, String id) {
		
		boolean entityNotFound = this.condensed.containsAllFields(entityName) == false;
		
		if(entityNotFound) {
			return false;
		}
		
		CcpJsonRepresentation innerJson = this.condensed.getInnerJsonFromPath(entityName, id);
		
		boolean idNotFound = innerJson.isEmpty();
		
		if(idNotFound) {
			return false;
		}
		
		return true;
	}
	
	public <T> T handleRecordInUnionAll(
			CcpJsonRepresentation searchParameter, 
			CcpHandleWithSearchResultsInTheEntity<T> handler
			) {
		
		CcpEntity entity = handler.getEntityToSearch();
	
		boolean recordNotFound = entity.isPresentInThisUnionAll(this, searchParameter) == false;
		
		if(recordNotFound) {
			T whenRecordWasNotFoundInTheEntitySearch = handler.whenRecordWasNotFoundInTheEntitySearch(searchParameter);
			return whenRecordWasNotFoundInTheEntitySearch; 
		}
		
		CcpJsonRepresentation requiredEntityRow = entity.getRequiredEntityRow(this, searchParameter);
		CcpJsonRepresentation onlyExistingFieldsAndHandledJson = entity.getOnlyExistingFieldsAndHandledJson(searchParameter);
		CcpJsonRepresentation recordFound = onlyExistingFieldsAndHandledJson.putAll(requiredEntityRow);
		
		CcpJsonRepresentation apply = recordFound.putAll(searchParameter);
		
		T whenRecordWasFoundInTheEntitySearch = handler.whenRecordWasFoundInTheEntitySearch(apply, recordFound);
		
		return whenRecordWasFoundInTheEntitySearch;
	}
	
	public CcpJsonRepresentation getEntityRow(String index, String id) {
		
		boolean indexNotFound = this.condensed.containsAllFields(index) == false;
		
		if(indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(index);

		boolean idNotFound = innerJson.containsAllFields(id) == false;
		
		if(idNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation jsonValue = innerJson.getInnerJson(id);
		return jsonValue;
	}

	public String toString() {
		return this.condensed.toString();
	}
	

}
