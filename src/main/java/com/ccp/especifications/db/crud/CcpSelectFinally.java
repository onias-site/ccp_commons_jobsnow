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

/**
 * Final step of the search fluent chain: runs the declared statements against a single union-all search and returns
 * the requested fields.
 */
public class CcpSelectFinally {

	/** Fields of the statements and of the result. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** The entity of a statement. */
		entity,
		/** The action of a statement. */
		action,
		/** The status of a statement, or of the error details. */
		status,
		/** The message of the error details. */
		message,
		/** The description of the declared statements, added to the error context. */
		flow,
		/** Who started the search, added to the result. */
		origin
	}

	/** The search parameters; the first one is the JSON that flows through the actions. */
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	/** The declared statements. */
	private final CcpJsonRepresentation statements;
	/** The fields kept in the result. */
	private final CcpJsonFieldName[] fields;

	/**
	 * Keeps the state of the search.
	 * @param parametersToSearch the search parameters
	 * @param statements the declared statements
	 * @param fields the fields kept in the result
	 */
	CcpSelectFinally(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements, CcpJsonFieldName[] fields) {
		this.parametersToSearch = parametersToSearch;
		this.fields = fields;
		this.statements = statements;
	}

	/**
	 * Runs the search (see {@code findById}) discarding the resulting data.
	 * @param context identifies who started the search (goes to the error messages and to the {@code origin} field)
	 * @param whenFlowError business run over the error context before the {@code CcpErrorFlowDisturb} is thrown
	 * @param whenFlowSuccess business run over the final JSON when no status interrupted the flow
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate before the search
	 * @return this step
	 */
	public CcpSelectFinally endThisProcedure(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementsCount = statements.size();
		CcpJsonRepresentation[] statementsArray = statements.toArray(new CcpJsonRepresentation[statementsCount]);
		this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, statementsArray);
		return this;
	}

	/**
	 * Runs the search (see {@code findById}) and returns the resulting data.
	 * @param context identifies who started the search (goes to the error messages and to the {@code origin} field)
	 * @param whenFlowError business run over the error context before the {@code CcpErrorFlowDisturb} is thrown
	 * @param whenFlowSuccess business run over the final JSON when no status interrupted the flow
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate before the search
	 * @return the requested fields of the final JSON plus {@code origin}
	 */
	public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementsCount = statements.size();
		CcpJsonRepresentation[] statementsArray = statements.toArray(new CcpJsonRepresentation[statementsCount]);
		CcpJsonRepresentation resultingData = this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, statementsArray);
		return resultingData;
	}

	/**
	 * Runs the statements:
	 * <ol>
	 * <li>searches, in one union-all, every entity mentioned by the statements, for every search parameter;</li>
	 * <li>starting from the first search parameter, evaluates each statement in order:
	 * <ul>
	 * <li>a statement without entity runs its action over the JSON;</li>
	 * <li>a statement whose presence expectation does not hold is skipped, but a record that exists is still added
	 * under {@code _entities.<entity>} (this is how {@code loadThisIdFromEntity} loads data);</li>
	 * <li>a statement whose expectation holds and has a status: the error context (JSON plus
	 * {@code errorDetails.message}/{@code errorDetails.status} and the record) goes through {@code whenFlowError}, the
	 * flow description is added under {@code flow} and a {@code CcpErrorFlowDisturb} with that status is thrown;</li>
	 * <li>a statement whose expectation holds and has an action runs it, with the record under
	 * {@code _entities.<entity>} when the expectation was "present".</li>
	 * </ul></li>
	 * <li>runs {@code whenFlowSuccess} and returns the requested fields plus {@code origin}.</li>
	 * </ol>
	 * The check that at least one field was requested happens first, before the search and the actions (until 2026-10-07
	 * it happened after the actions, with their side effects already done).
	 * <p>
	 * Only the first search parameter flows through the statements, by design: it is the request itself; the other
	 * parameters exist to compute the keys of other entities for the union-all, and their data reach the statements only
	 * through the records found ({@code _entities}).
	 * @param origin identifies who started the search
	 * @param whenFlowError business run over the error context
	 * @param whenFlowSuccess business run over the final JSON
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @param specifications the statements
	 * @return the requested fields plus {@code origin}
	 * @throws CcpErrorFlowFieldsToReturnNotMentioned when no field was requested
	 */
	private CcpJsonRepresentation findById(
			CcpJsonFieldName origin,
			CcpBusiness whenFlowError,
			CcpBusiness whenFlowSuccess,
			Consumer<String[]> functionToDeleteKeysInTheCache,
			CcpJsonRepresentation... specifications) {
		// before the search and the actions: until 2026-10-07 this check came at the end, after the actions had already
		// written, sent or deleted, so a procedure without fields failed only after its side effects
		boolean zeroFields = this.fields.length <= 0;

		if (zeroFields) {
			CcpErrorFlowFieldsToReturnNotMentioned fieldsNotMentionedError = new CcpErrorFlowFieldsToReturnNotMentioned(origin);
			throw fieldsNotMentionedError;
		}
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

		CcpJsonRepresentation successResult = whenFlowSuccess.execute(json);
		CcpJsonRepresentation jsonPiece = successResult.getJsonPiece(this.fields);
		CcpJsonRepresentation resultingData = jsonPiece.put(JsonFieldNames.origin, origin);

		return resultingData;
	}
	
	/**
	 * Tells whether any search parameter that has the complete primary key of the entity found a record in it.
	 * @param unionAll the search result
	 * @param entity the entity checked
	 * @return {@code true} when the record was found
	 */
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
	
	/**
	 * Returns the record found for the first search parameter that has the complete primary key of the entity.
	 * @param unionAll the search result
	 * @param entity the entity
	 * @return the record, or an empty JSON when none was found
	 */
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
