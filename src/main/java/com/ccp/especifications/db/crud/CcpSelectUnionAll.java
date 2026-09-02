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
 * Representa o resultado condensado de uma busca {@code unionAll} — múltiplas entidades buscadas
 * em uma única chamada ao banco. Internamente organiza os resultados em um mapa aninhado
 * {@code { entidade → { id → dadosDoRegistro } }}.
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
					Supplier<CcpJsonRepresentation> supplier = searchParameter.getJsonSupplier();
					CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(supplier);
					CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entity, CcpEntityDecoratorTypes.FieldsValidator);
					CcpJsonRepresentation handledJson = customEntity.getHandledJson(primaryKeyValues);
					String id = entity.calculateId(handledJson);
					CcpFieldName ccpFieldName = new CcpFieldName(id);
					explainedSearch = explainedSearch.addToItem(entity, ccpFieldName, primaryKeyValues);
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {
				}
			}
		}

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();

		CcpJsonRepresentation condensed = CcpOtherConstants.EMPTY_JSON;

		for (CcpJsonRepresentation result : results) {
			CcpFieldName ccpFieldName2 = new CcpFieldName(fieldNameToId);
			String id = result.getAsString(ccpFieldName2);
			CcpFieldName ccpFieldName3 = new CcpFieldName(fieldNameToEntity);
			String entityName = result.getAsString(ccpFieldName3);
			CcpFieldName ccpFieldName4 = new CcpFieldName(fieldNameToEntity);
			CcpFieldName ccpFieldName5 = new CcpFieldName(fieldNameToId);
			CcpJsonRepresentation removeKeys = result.removeFields(ccpFieldName4, ccpFieldName5);
			CcpFieldName ccpFieldName6 = new CcpFieldName(entityName);
			CcpFieldName ccpFieldName7 = new CcpFieldName(id);
			CcpJsonRepresentation innerJsonFromPath = explainedSearch.getInnerJsonFromPath(ccpFieldName6, ccpFieldName7);
			CcpFieldName ccpFieldName8 = new CcpFieldName(entityName);
			CcpFieldName ccpFieldName9 = new CcpFieldName(id);
			condensed = condensed.addToItem(ccpFieldName8, ccpFieldName9, removeKeys);
			CcpFieldName ccpFieldName10 = new CcpFieldName(entityName);
			String explainedSearchMais = JsonFieldNames.explainedSearch + ".";
			String explainedSearchMaisMais = explainedSearchMais + id;
			CcpFieldName ccpFieldName11 = new CcpFieldName(explainedSearchMaisMais);
			condensed = condensed.addToItem(ccpFieldName10, ccpFieldName11, innerJsonFromPath);
		}
		this.condensed = condensed;
	}

	public boolean isPresent(String entityName, String id) {
		CcpFieldName ccpFieldName12 = new CcpFieldName(entityName);
		boolean containsAllFields = this.condensed.containsAllFields(ccpFieldName12);
		boolean entityNotFound = false == containsAllFields;
		if (entityNotFound) {
			return false;
		}
		CcpFieldName ccpFieldName13 = new CcpFieldName(entityName);
		CcpFieldName ccpFieldName14 = new CcpFieldName(id);
		CcpJsonRepresentation innerJson = this.condensed.getInnerJsonFromPath(ccpFieldName13, ccpFieldName14);
		boolean idNotFound = innerJson.isEmpty();
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
		CcpFieldName ccpFieldName15 = new CcpFieldName(index);
		boolean containsAllFields2 = this.condensed.containsAllFields(ccpFieldName15);
		boolean indexNotFound = false == containsAllFields2;
		if (indexNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpFieldName ccpFieldName16 = new CcpFieldName(index);
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(ccpFieldName16);
		CcpFieldName ccpFieldName17 = new CcpFieldName(id);
		boolean containsAllFields3 = innerJson.containsAllFields(ccpFieldName17);
		boolean idNotFound = false == containsAllFields3;
		if (idNotFound) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		CcpFieldName ccpFieldName18 = new CcpFieldName(id);
		CcpJsonRepresentation jsonValue = innerJson.getInnerJson(ccpFieldName18);
		return jsonValue;
	}

	public String toString() {
		String toString = this.condensed.toString();
		return toString;
	}

	public List<CcpJsonRepresentation> getEntityRows(CcpEntity entity) {
		boolean containsAllFields4 = this.condensed.containsAllFields(entity);
		boolean indexNotFound = false == containsAllFields4;
		if (indexNotFound) {
			return new ArrayList<>();
		}
		CcpJsonRepresentation innerJson = this.condensed.getInnerJson(entity);
		Set<String> fieldSet = innerJson.fieldSet();
		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		String fieldNameToId = dependency.getFieldNameToId();
		Stream<String> stream = fieldSet.stream();
		var streamMap = stream.map(id -> innerJson.getInnerJson(new CcpFieldName(id)).put(new CcpFieldName(fieldNameToId), id));
		List<CcpJsonRepresentation> collect = streamMap.collect(Collectors.toList());
		return collect;
	}
}
