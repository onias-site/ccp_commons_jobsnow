package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
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
					CcpEntityMetaData entityDetails = entity.getEntityMetaData();
					Supplier<CcpJsonRepresentation> supplier = searchParameter.getJsonSupplier();
					CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(supplier);
					CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entity, CcpEntityDecoratorTypes.FieldsValidator);
					CcpJsonRepresentation handledJson = customEntity.getHandledJson(primaryKeyValues);
					String id = entity.calculateId(handledJson);
					explainedSearch = explainedSearch.addToItem(entity, new CcpFieldName(id), primaryKeyValues);
	
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {

				}
			}
		}
		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId(); 

		CcpJsonRepresentation  condensed = CcpOtherConstants.EMPTY_JSON;
	
		for (CcpJsonRepresentation result : results) {
			String id = result.getAsString(new CcpFieldName(fieldNameToId));
			String entityName = result.getAsString(new CcpFieldName(fieldNameToEntity));
			CcpJsonRepresentation removeKeys = result.removeFields(new CcpFieldName(fieldNameToEntity), new CcpFieldName(fieldNameToId));
			CcpJsonRepresentation innerJsonFromPath = explainedSearch.getInnerJsonFromPath(new CcpFieldName(entityName), new CcpFieldName(id));
			condensed = condensed.addToItem(new CcpFieldName(entityName), new CcpFieldName(id), removeKeys);
			condensed = condensed.addToItem(new CcpFieldName(entityName), new CcpFieldName(JsonFieldNames.explainedSearch + "." + id), innerJsonFromPath);
			
		}
		this.condensed = condensed;
	}
	
	public boolean isPresent(String entityName, String id) {
		
		boolean entityNotFound = false == this.condensed.containsAllFields(new CcpFieldName(entityName));

		if(entityNotFound) {
			return false;
		}

		CcpJsonRepresentation innerJson = this.condensed.getInnerJsonFromPath(new CcpFieldName(entityName), new CcpFieldName(id));
		
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
		
		Supplier<CcpJsonRepresentation> jsonSupplier = searchParameter.getJsonSupplier();
		CcpJsonRepresentation recordFound = entity.getRecordFromUnionAll(this, jsonSupplier);
		
		CcpJsonRepresentation apply = recordFound.mergeWithAnotherJson(handledJson);
		
		T whenRecordWasFoundInTheEntitySearch = handler.whenRecordWasFoundInTheEntitySearch(apply, recordFound);
		
		return whenRecordWasFoundInTheEntitySearch;
	}
	
	public CcpJsonRepresentation getEntityRow(String index, String id) {
		
		boolean indexNotFound = false == this.condensed.containsAllFields(new CcpFieldName(index));

		if(indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(new CcpFieldName(index));

		boolean idNotFound = false == innerJson.containsAllFields(new CcpFieldName(id));

		if(idNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		CcpJsonRepresentation jsonValue = innerJson.getInnerJson(new CcpFieldName(id));
		return jsonValue;
	}

	public String toString() {
		return this.condensed.toString();
	}
	
	public List<CcpJsonRepresentation> getEntityRows(CcpEntity entity){

		boolean indexNotFound = false == this.condensed.containsAllFields(entity);

		if(indexNotFound) {
			return new ArrayList<>();
		}

		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(entity);
		Set<String> fieldSet = innerJson.fieldSet();

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		String fieldNameToId = dependency.getFieldNameToId();
		List<CcpJsonRepresentation> collect = fieldSet.stream().map(id -> innerJson.getInnerJson(new CcpFieldName(id)).put(new CcpFieldName(fieldNameToId), id)).collect(Collectors.toList());
		return collect;
	}
	

}
