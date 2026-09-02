package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatus;
import java.util.stream.Stream;

public class CcpSelectFinally {

	enum JsonFieldNames implements CcpJsonFieldName {
		statements, entity, action, found, status, message, errorDetails, flow, origin
	}

	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;
	private final CcpJsonFieldName[] fields;

	CcpSelectFinally(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements, CcpJsonFieldName[] fields) {
		this.parametersToSearch = parametersToSearch;
		this.fields = fields;
		this.statements = statements;
	}

	public CcpSelectFinally endThisProcedure(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
		int statementsSize = statements.size();
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statementsSize]);
		this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
		return this;
	}

	public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
		int statementsSize2 = statements.size();
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statementsSize2]);
		CcpJsonRepresentation findById = this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
		return findById;
	}

	private CcpJsonRepresentation findById(
			CcpJsonFieldName origin,
			CcpBusiness whenFlowError,
			CcpBusiness whenFlowSuccess,
			Consumer<String[]> functionToDeleteKeysInTheCache,
			CcpJsonRepresentation... specifications) {
				Stream<CcpJsonRepresentation> stream = Arrays.asList(specifications).stream();
				var filter = stream
				.filter(x -> x.containsAllFields(JsonFieldNames.entity));
				var filterMap = filter
				.map(x -> (CcpEntity) x.get(JsonFieldNames.entity));

				List<CcpEntity> keySet = filterMap
				.collect(Collectors.toList());

		LinkedHashSet<CcpEntity> set = new LinkedHashSet<>(keySet);
		int setSize = set.size();
		CcpEntity[] entities = set.toArray(new CcpEntity[setSize]);

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		int parametersToSearchSize = this.parametersToSearch.size();

		CcpJsonRepresentation[] jsons = this.parametersToSearch.toArray(new CcpJsonRepresentation[parametersToSearchSize]);

		CcpSelectUnionAll unionAll = crud.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		
		CcpJsonRepresentation json = jsons[0];

		for (CcpJsonRepresentation specification : specifications) {
			boolean containsField = specification.containsField(JsonFieldNames.entity);

			boolean executeFreeAction = false == containsField;

			if (executeFreeAction) {
				CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
				json = action.execute(json);
				continue;
			}

			boolean shouldHaveBeenFound = specification.getAsBoolean(JsonFieldNames.found);
			CcpEntity entity = specification.getAsObject(JsonFieldNames.entity);

			boolean wasActuallyFound = this.isPresentInUnionAll(unionAll, entity);

			boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
			CcpEntityMetaData entityMetaData = entity.getEntityMetaData();

			if (itWasNotForeseen) {
				boolean valorIgual = false == wasActuallyFound;
				if (valorIgual) {
					continue;
				}

				CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
				json = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
				continue;
			}
			boolean containsField2 = specification.containsField(JsonFieldNames.action);

			boolean willNotExecuteAction = false == containsField2;

			if (willNotExecuteAction) {
				boolean containsField3 = specification.containsField(JsonFieldNames.status);
				boolean willNotThrowException = false == containsField3;
				if (willNotThrowException) {
					continue;
				}
				CcpProcessStatus status = specification.getAsObject(JsonFieldNames.status);
				String message = specification.getOrDefault(JsonFieldNames.message, () -> status.name());
				CcpJsonRepresentation addToItem = json.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.message, message);
				CcpJsonRepresentation put = addToItem
						.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.status, status);
				CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
				CcpJsonRepresentation context = put.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
				CcpJsonRepresentation apply = whenFlowError.execute(context);
				Stream<CcpJsonRepresentation> stream2 = Arrays.asList(specifications).stream();
				var stream2Map = stream2
						.map(j -> j.whenAnyFieldsAreFound(FunctionPutEntity.INSTANCE, JsonFieldNames.entity));
						var stream2MapMap = stream2Map
						.map(j -> j.whenAnyFieldsAreFound(FunctionPutStatus.INSTANCE, JsonFieldNames.status));
						List<CcpJsonRepresentation> asList = stream2MapMap
						.collect(Collectors.toList());
				CcpJsonRepresentation result = apply.put(JsonFieldNames.flow, asList);
				String valorMais = "Context: " + origin;
				String valorMaisMais = valorMais + ". Entity: ";
				String valorMaisMaisMais = valorMaisMais + entityMetaData.entityName;
				String valorMaisMaisMaisMais = valorMaisMaisMais
						+ ". status: ";
						String valorMaisMaisMaisMaisMais = valorMaisMaisMaisMais + status;
						String valorMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMais
						+ ". shouldHaveBeenFound: ";
						String valorMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMais + shouldHaveBeenFound;
						String valorMaisMaisMaisMaisMaisMaisMaisMais = valorMaisMaisMaisMaisMaisMaisMais + ". wasActuallyFound: ";

						String reason = valorMaisMaisMaisMaisMaisMaisMaisMais + wasActuallyFound;
						CcpErrorFlowDisturb ccpErrorFlowDisturb = new CcpErrorFlowDisturb(result, status, reason, this.fields);

						throw ccpErrorFlowDisturb;
			}

			CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
			boolean valorIgual2 = false == shouldHaveBeenFound;

			if (valorIgual2) {
				json = action.execute(json);
				continue;
			}
			CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
			CcpJsonRepresentation context = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
			json = action.execute(context);
		}

		boolean zeroFields = this.fields.length <= 0;
		
		if (zeroFields) {
			CcpErrorFlowFieldsToReturnNotMentioned ccpErrorFlowFieldsToReturnNotMentioned = new CcpErrorFlowFieldsToReturnNotMentioned(origin);
			throw ccpErrorFlowFieldsToReturnNotMentioned;
		}
		
		CcpJsonRepresentation apply = whenFlowSuccess.execute(json);
		CcpJsonRepresentation jsonPiece = apply.getJsonPiece(this.fields);
		CcpJsonRepresentation subMap = jsonPiece.put(JsonFieldNames.origin, origin);

		return subMap;
	}
	
	private boolean isPresentInUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
		
		for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
			CcpEntityMetaData metaData = entity.getEntityMetaData();
			boolean containsAllFields = parameterToSearch.containsAllFields(metaData.primaryKeyNames);
			boolean isNotParameterToSearch = false == containsAllFields;
			if(isNotParameterToSearch) {
				continue;
			}
			boolean presentInThisUnionAll = entity.isPresentInThisUnionAll(unionAll, parameterToSearch);
			if(presentInThisUnionAll) {
				return true;
			}
		}
		return false;
	}
	
	private CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
		
		for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
			CcpEntityMetaData metaData = entity.getEntityMetaData();
			boolean containsAllFields2 = parameterToSearch.containsAllFields(metaData.primaryKeyNames);
			boolean isNotParameterToSearch = false == containsAllFields2;
			if(isNotParameterToSearch) {
				continue;
			}
			boolean presentInThisUnionAll2 = entity.isPresentInThisUnionAll(unionAll, parameterToSearch);

			boolean isNotPresentInThisUnionAll = false == presentInThisUnionAll2;
			
			if(isNotPresentInThisUnionAll) {
				continue;
			}
			
			Supplier<CcpJsonRepresentation> jsonSupplier = parameterToSearch.getJsonSupplier();
			CcpJsonRepresentation recordFromUnionAll = entity.getRecordFromUnionAll(unionAll, jsonSupplier);
			return recordFromUnionAll;
		}
		return CcpOtherConstants.EMPTY_JSON;
	}

}
