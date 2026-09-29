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

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

public class CcpSelectFinally {

	enum JsonFieldNames implements CcpJsonFieldName { entity, action, status, message, flow, origin
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
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementsCount = statements.size();
		CcpJsonRepresentation[] statementsArray = statements.toArray(new CcpJsonRepresentation[statementsCount]);
		this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, statementsArray);
		return this;
	}

	public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementsCount = statements.size();
		CcpJsonRepresentation[] statementsArray = statements.toArray(new CcpJsonRepresentation[statementsCount]);
		CcpJsonRepresentation resultingData = this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, statementsArray);
		return resultingData;
	}

	private CcpJsonRepresentation findById(
			CcpJsonFieldName origin,
			CcpBusiness whenFlowError,
			CcpBusiness whenFlowSuccess,
			Consumer<String[]> functionToDeleteKeysInTheCache,
			CcpJsonRepresentation... specifications) {
				Stream<CcpJsonRepresentation> specificationsStream = Arrays.asList(specifications).stream();
				var entitySpecificationsStream = specificationsStream
				.filter(x -> x.containsAllFields(JsonFieldNames.entity));
				var entitiesStream = entitySpecificationsStream
				.map(x -> (CcpEntity) x.get(JsonFieldNames.entity));

				List<CcpEntity> entitiesFound = entitiesStream
				.collect(Collectors.toList());

		LinkedHashSet<CcpEntity> distinctEntities = new LinkedHashSet<>(entitiesFound);
		int distinctEntitiesCount = distinctEntities.size();
		CcpEntity[] entities = distinctEntities.toArray(new CcpEntity[distinctEntitiesCount]);

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		int parametersToSearchSize = this.parametersToSearch.size();

		CcpJsonRepresentation[] jsons = this.parametersToSearch.toArray(new CcpJsonRepresentation[parametersToSearchSize]);

		CcpSelectUnionAll unionAll = crud.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		
		CcpJsonRepresentation json = jsons[0];

		for (CcpJsonRepresentation specification : specifications) {
			boolean hasEntity = specification.containsField(JsonFieldNames.entity);

			boolean executeFreeAction = false == hasEntity;

			if (executeFreeAction) {
				CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
				json = action.execute(json);
				continue;
			}

			boolean shouldHaveBeenFound = specification.getAsBoolean(CcpJsonCommonsFields.found);
			CcpEntity entity = specification.getAsObject(JsonFieldNames.entity);

			boolean wasActuallyFound = this.isPresentInUnionAll(unionAll, entity);

			boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
			CcpEntityMetaData entityMetaData = entity.getEntityMetaData();

			if (itWasNotForeseen) {
				boolean wasNotFound = false == wasActuallyFound;
				if (wasNotFound) {
					continue;
				}

				CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
				json = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
				continue;
			}
			boolean hasAction = specification.containsField(JsonFieldNames.action);

			boolean willNotExecuteAction = false == hasAction;

			if (willNotExecuteAction) {
				boolean hasStatus = specification.containsField(JsonFieldNames.status);
				boolean willNotThrowException = false == hasStatus;
				if (willNotThrowException) {
					continue;
				}
				CcpProcessStatus status = specification.getAsObject(JsonFieldNames.status);
				String message = specification.getOrDefault(JsonFieldNames.message, () -> status.name());
				CcpJsonRepresentation jsonWithErrorMessage = json.addToItem(CcpJsonCommonsFields.errorDetails, JsonFieldNames.message, message);
				CcpJsonRepresentation jsonWithErrorDetails = jsonWithErrorMessage
						.addToItem(CcpJsonCommonsFields.errorDetails, JsonFieldNames.status, status);
				CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
				CcpJsonRepresentation errorContext = jsonWithErrorDetails.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
				CcpJsonRepresentation errorResult = whenFlowError.execute(errorContext);
				Stream<CcpJsonRepresentation> specificationsStreamForFlow = Arrays.asList(specifications).stream();
				var flowWithEntityNames = specificationsStreamForFlow
						.map(j -> j.whenAnyFieldsAreFound(FunctionPutEntity.INSTANCE, JsonFieldNames.entity));
						var flowWithStatusNames = flowWithEntityNames
						.map(j -> j.whenAnyFieldsAreFound(FunctionPutStatus.INSTANCE, JsonFieldNames.status));
						List<CcpJsonRepresentation> flowDescription = flowWithStatusNames
						.collect(Collectors.toList());
				CcpJsonRepresentation result = errorResult.put(JsonFieldNames.flow, flowDescription);
				String reasonWithContext = "Context: " + origin;
				String reasonWithEntityLabel = reasonWithContext + ". Entity: ";
				String reasonWithEntity = reasonWithEntityLabel + entityMetaData.entityName;
				String reasonWithStatusLabel = reasonWithEntity
						+ ". status: ";
						String reasonWithStatus = reasonWithStatusLabel + status;
						String reasonWithShouldHaveBeenFoundLabel = reasonWithStatus
						+ ". shouldHaveBeenFound: ";
						String reasonWithShouldHaveBeenFound = reasonWithShouldHaveBeenFoundLabel + shouldHaveBeenFound;
						String reasonWithWasActuallyFoundLabel = reasonWithShouldHaveBeenFound + ". wasActuallyFound: ";

						String reason = reasonWithWasActuallyFoundLabel + wasActuallyFound;
						CcpErrorFlowDisturb flowDisturbError = new CcpErrorFlowDisturb(result, status, reason, this.fields);

						throw flowDisturbError;
			}

			CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
			boolean shouldNotHaveBeenFound = false == shouldHaveBeenFound;

			if (shouldNotHaveBeenFound) {
				json = action.execute(json);
				continue;
			}
			CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
			CcpJsonRepresentation context = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
			json = action.execute(context);
		}

		boolean zeroFields = this.fields.length <= 0;
		
		if (zeroFields) {
			CcpErrorFlowFieldsToReturnNotMentioned fieldsNotMentionedError = new CcpErrorFlowFieldsToReturnNotMentioned(origin);
			throw fieldsNotMentionedError;
		}
		
		CcpJsonRepresentation successResult = whenFlowSuccess.execute(json);
		CcpJsonRepresentation jsonPiece = successResult.getJsonPiece(this.fields);
		CcpJsonRepresentation resultingData = jsonPiece.put(JsonFieldNames.origin, origin);

		return resultingData;
	}
	
	private boolean isPresentInUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
		
		for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
			CcpEntityMetaData metaData = entity.getEntityMetaData();
			boolean containsPrimaryKey = parameterToSearch.containsAllFields(metaData.primaryKeyNames);
			boolean isNotParameterToSearch = false == containsPrimaryKey;
			if(isNotParameterToSearch) {
				continue;
			}
			boolean isPresent = entity.isPresentInThisUnionAll(unionAll, parameterToSearch);
			if(isPresent) {
				return true;
			}
		}
		return false;
	}
	
	private CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
		
		for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
			CcpEntityMetaData metaData = entity.getEntityMetaData();
			boolean containsPrimaryKey = parameterToSearch.containsAllFields(metaData.primaryKeyNames);
			boolean isNotParameterToSearch = false == containsPrimaryKey;
			if(isNotParameterToSearch) {
				continue;
			}
			boolean isPresent = entity.isPresentInThisUnionAll(unionAll, parameterToSearch);

			boolean isNotPresentInThisUnionAll = false == isPresent;
			
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
