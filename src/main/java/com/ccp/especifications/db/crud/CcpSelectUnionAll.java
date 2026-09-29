package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpErrorEntityPrimaryKeyIsMissing;
import java.util.stream.Stream;

/**
 * Represents the condensed result of a {@code unionAll} search — several entities searched
 * in a single call to the database. Internally it organizes the results in a nested map
 * {@code { entityUnderTest → { id → recordData } }}, where each record carries in
 * {@code explainedSearch} the primary key that located it.
 */
public class CcpSelectUnionAll {

	enum JsonFieldNames implements CcpJsonFieldName {
		explainedSearch
	}

	public final CcpJsonRepresentation condensed;

	public CcpSelectUnionAll(CcpJsonRepresentation[] searchParameters, List<CcpJsonRepresentation> results, CcpEntity... entities) {

		CcpJsonRepresentation explainedSearch = CcpOtherConstants.EMPTY_JSON;

		for (CcpEntity entity : entities) {
			for (var searchParameter : searchParameters) {
				try {
					CcpEntityMetaData entityDetails = entity.getEntityMetaData();
					Supplier<CcpJsonRepresentation> jsonSupplier = searchParameter.getJsonSupplier();
					CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(jsonSupplier);
					CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entity, CcpEntityDecoratorTypes.FieldsValidator);
					CcpJsonRepresentation handledJson = customEntity.getHandledJson(primaryKeyValues);
					String id = entity.calculateId(handledJson);
					CcpFieldName idKey = new CcpFieldName(id);
					explainedSearch = explainedSearch.addToItem(entity, idKey, primaryKeyValues);
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {
				}
			}
		}

		CcpDbRequester dbRequester = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		String fieldNameToEntity = dbRequester.getFieldNameToEntity();
		String fieldNameToId = dbRequester.getFieldNameToId();

		CcpJsonRepresentation condensed = CcpOtherConstants.EMPTY_JSON;

		for (CcpJsonRepresentation result : results) {
			CcpFieldName idKey = new CcpFieldName(fieldNameToId);
			String id = result.getAsString(idKey);
			CcpFieldName entityKey = new CcpFieldName(fieldNameToEntity);
			String entityName = result.getAsString(entityKey);
			CcpFieldName entityKeyToRemove = new CcpFieldName(fieldNameToEntity);
			CcpFieldName idKeyToRemove = new CcpFieldName(fieldNameToId);
			CcpJsonRepresentation recordWithoutEntityAndId = result.removeFields(entityKeyToRemove, idKeyToRemove);
			CcpFieldName entityNamePathStep = new CcpFieldName(entityName);
			CcpFieldName idPathStep = new CcpFieldName(id);
			CcpJsonRepresentation recordExplainedSearch = explainedSearch.getInnerJsonFromPath(entityNamePathStep, idPathStep);
			CcpJsonRepresentation recordWithExplainedSearch = recordWithoutEntityAndId.put(JsonFieldNames.explainedSearch, recordExplainedSearch);
			CcpFieldName entityNameGroup = new CcpFieldName(entityName);
			CcpFieldName idGroup = new CcpFieldName(id);
			condensed = condensed.addToItem(entityNameGroup, idGroup, recordWithExplainedSearch);
		}
		this.condensed = condensed;
	}

	public boolean isPresent(String entityName, String id) {
		CcpJsonRepresentation entityRow = this.getEntityRow(entityName, id);
		boolean idNotFound = entityRow.isEmpty();
		if (idNotFound) {
			return false;
		}
		return true;
	}

	public <T> T handleRecordInUnionAll(CcpJsonRepresentation searchParameter, CcpHandleWithSearchResultsInTheEntity<T> handler) {
		CcpEntity entity = handler.getEntityToSearch();
		boolean presentInThisUnionAll = entity.isPresentInThisUnionAll(this, searchParameter);
		boolean recordNotFound = false == presentInThisUnionAll;
		CcpJsonRepresentation handledJson = entity.getHandledJson(searchParameter);
		if (recordNotFound) {
			T notFoundResult = handler.whenRecordWasNotFoundInTheEntitySearch(handledJson);
			return notFoundResult;
		}
		Supplier<CcpJsonRepresentation> jsonSupplier = searchParameter.getJsonSupplier();
		CcpJsonRepresentation recordFound = entity.getRecordFromUnionAll(this, jsonSupplier);
		CcpJsonRepresentation recordMergedWithSearch = recordFound.mergeWithAnotherJson(handledJson);
		T foundResult = handler.whenRecordWasFoundInTheEntitySearch(recordMergedWithSearch, recordFound);
		return foundResult;
	}

	public CcpJsonRepresentation getEntityRow(String index, String id) {
		CcpFieldName indexKey = new CcpFieldName(index);
		boolean containsIndex = this.condensed.containsAllFields(indexKey);
		boolean indexNotFound = false == containsIndex;
		if (indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpFieldName indexKeyToRead = new CcpFieldName(index);
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(indexKeyToRead);
		CcpFieldName idKey = new CcpFieldName(id);
		boolean containsId = innerJson.containsAllFields(idKey);
		boolean idNotFound = false == containsId;
		if (idNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpFieldName idKeyToRead = new CcpFieldName(id);
		CcpJsonRepresentation jsonValue = innerJson.getInnerJson(idKeyToRead);
		CcpJsonRepresentation withoutExplainedSearch = jsonValue.removeFields(JsonFieldNames.explainedSearch);
		return withoutExplainedSearch;
	}

	public String toString() {
		String condensedAsString = this.condensed.toString();
		return condensedAsString;
	}

	public List<CcpJsonRepresentation> getEntityRows(CcpEntity entity) {
		boolean containsEntity = this.condensed.containsAllFields(entity);
		boolean indexNotFound = false == containsEntity;
		if (indexNotFound) {
			return new ArrayList<>();
		}
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(entity);
		Set<String> fieldSet = innerJson.fieldSet();
		CcpDbRequester dbRequester = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		String fieldNameToId = dbRequester.getFieldNameToId();
		Stream<String> idsStream = fieldSet.stream();
		var entityRowsStream = idsStream.map(id -> innerJson.getInnerJson(new CcpFieldName(id)).removeFields(JsonFieldNames.explainedSearch).put(new CcpFieldName(fieldNameToId), id));
		List<CcpJsonRepresentation> entityRows = entityRowsStream.collect(Collectors.toList());
		return entityRows;
	}

	/**
	 * Returns the records found for the given entity in the format {@code { id → recordData }},
	 * without the {@code explainedSearch} that comes with each record in the condensed map.
	 */
	public CcpJsonRepresentation getEntityRowsGroupedById(CcpEntity entity) {
		boolean containsEntity = this.condensed.containsAllFields(entity);
		boolean indexNotFound = false == containsEntity;
		if (indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(entity);
		Set<String> fieldSet = innerJson.fieldSet();
		CcpJsonRepresentation groupedById = CcpOtherConstants.EMPTY_JSON;
		for (String id : fieldSet) {
			CcpFieldName idKey = new CcpFieldName(id);
			CcpJsonRepresentation entityRow = innerJson.getInnerJson(idKey);
			CcpJsonRepresentation withoutExplainedSearch = entityRow.removeFields(JsonFieldNames.explainedSearch);
			groupedById = groupedById.put(idKey, withoutExplainedSearch);
		}
		return groupedById;
	}
}
