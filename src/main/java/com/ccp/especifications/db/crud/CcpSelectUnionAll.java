package com.ccp.especifications.db.crud;

import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

public class CcpSelectUnionAll {
	
	enum JsonFieldNames implements CcpJsonFieldName{
		explainedSearch
	}
	
	
	public final CcpJsonRepresentation  condensed;

	public CcpSelectUnionAll(CcpJsonRepresentation[] searchParameters, List<CcpJsonRepresentation> results, CcpEntity... entities) {
		
		CcpJsonRepresentation explainedSearch = CcpOtherConstants.EMPTY_JSON;
		
		for (CcpEntity entity : entities) {
			for (var searchParameter : searchParameters) {
				try {
					CcpEntityDetails entityDetails = entity.getEntityDetails();
					CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(searchParameter);
					CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entity, CcpEntityDecoratorTypes.FieldsValidator);
					CcpJsonRepresentation handledJson = customEntity.getHandledJson(primaryKeyValues);
					String id = entity.calculateId(handledJson);
					explainedSearch = explainedSearch.getDynamicVersion().addToItem(entityDetails.entityName, id, primaryKeyValues);
	
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {

				}
			}
		}
		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId(); 

		CcpJsonRepresentation  condensed = CcpOtherConstants.EMPTY_JSON;
	
		for (CcpJsonRepresentation result : results) {
			String id = result.getDynamicVersion().getAsString(fieldNameToId);
			String entityName = result.getDynamicVersion().getAsString(fieldNameToEntity);
			CcpJsonRepresentation removeKeys = result.getDynamicVersion().removeFields(fieldNameToEntity, fieldNameToId);
			CcpJsonRepresentation innerJsonFromPath = explainedSearch.getDynamicVersion().getInnerJsonFromPath(entityName, id);
			condensed = condensed.getDynamicVersion().addToItem(entityName, id, removeKeys);
			condensed = condensed.getDynamicVersion().addToItem(entityName, JsonFieldNames.explainedSearch + "." + id, innerJsonFromPath);
			
		}
		this.condensed = condensed;
	}
	
	public boolean isPresent(String entityName, String id) {
		
		boolean entityNotFound = false == this.condensed.getDynamicVersion().containsAllFields(entityName);
		
		if(entityNotFound) {
			return false;
		}
		
		CcpJsonRepresentation innerJson = this.condensed.getDynamicVersion().getInnerJsonFromPath(entityName, id);
		
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
	
		boolean recordNotFound = false == entity.isPresentInThisUnionAll(this, searchParameter);
		
		CcpJsonRepresentation handledJson = entity.getHandledJson(searchParameter);

		if(recordNotFound) {
			T whenRecordWasNotFoundInTheEntitySearch = handler.whenRecordWasNotFoundInTheEntitySearch(handledJson);
			return whenRecordWasNotFoundInTheEntitySearch; 
		}
		
		CcpJsonRepresentation recordFound = entity.getRecordFromUnionAll(this, searchParameter);
		
		CcpJsonRepresentation apply = recordFound.mergeWithAnotherJson(handledJson);
		
		T whenRecordWasFoundInTheEntitySearch = handler.whenRecordWasFoundInTheEntitySearch(apply, recordFound);
		
		return whenRecordWasFoundInTheEntitySearch;
	}
	
	public CcpJsonRepresentation getEntityRow(String index, String id) {
		
		boolean indexNotFound = false == this.condensed.getDynamicVersion().containsAllFields(index);
		
		if(indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation innerJson = this.condensed.getDynamicVersion().getInnerJson(index);

		boolean idNotFound = false == innerJson.getDynamicVersion().containsAllFields(id);
		
		if(idNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		CcpJsonRepresentation jsonValue = innerJson.getDynamicVersion().getInnerJson(id);
		return jsonValue;
	}

	public String toString() {
		return this.condensed.toString();
	}
	

}
