package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;
import com.ccp.business.*;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatus;

public class CcpSelectFinally {
	enum JsonFieldNames implements CcpJsonFieldName{
		statements, entity, action, found, _entities, status, message, errorDetails, flow, origin
		
	}
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;
	private final String[] fields;
	
	CcpSelectFinally(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements, String[] fields) {
		this.parametersToSearch = parametersToSearch;
		this.fields = fields;
		this.statements = statements;
	}

	public CcpSelectFinally endThisProcedure(String context, CcpBusiness whenFlowError,CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
		this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
		return this; 
	}

	public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(String context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache
			) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
		CcpJsonRepresentation findById = this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
		return findById;
	}

	
	private CcpJsonRepresentation findById( 
			String origin,
			CcpBusiness whenFlowError, 
			CcpBusiness whenFlowSuccess, 
			Consumer<String[]> functionToDeleteKeysInTheCache, 
			CcpJsonRepresentation... specifications) {
		
		List<CcpEntity> keySet = Arrays.asList(specifications).stream()
				.filter(x -> x.containsAllFields(JsonFieldNames.entity))
				.map(x -> (CcpEntity) x.get(JsonFieldNames.entity) ) 
				.collect(Collectors.toList());
		
		LinkedHashSet<CcpEntity> set = new LinkedHashSet<>(keySet);

		CcpEntity[] entities = set.toArray(new CcpEntity[set.size()]);
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class); 
		
		CcpJsonRepresentation[] jsons = this.parametersToSearch.toArray(new CcpJsonRepresentation[this.parametersToSearch.size()]);
		
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation jsn : jsons) {
			json = json.mergeWithAnotherJson(jsn);
		}
		
		CcpSelectUnionAll unionAll = crud.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		
		for (CcpJsonRepresentation specification : specifications) {
			
			boolean executeFreeAction = false == specification.containsField(JsonFieldNames.entity);
			
			if(executeFreeAction) {
				CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
				json = action.apply(json);
				continue;
			}
			
			boolean shouldHaveBeenFound = specification.getAsBoolean(JsonFieldNames.found);
			
			CcpEntity entity = specification.getAsObject(JsonFieldNames.entity);
			
			boolean wasActuallyFound;
			
			try {
				wasActuallyFound = entity.isPresentInThisUnionAll(unionAll, json);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
			
			CcpEntityDetails entityDetails = entity.getEntityDetails();
			if(itWasNotForeseen) {

				if(false == wasActuallyFound) {
					continue;
				}
				String entityName = entityDetails.entityName;
				try {
					CcpJsonRepresentation dataBaseRow = entity.getRecordFromUnionAll(unionAll, json);
					json = json.addToItem(JsonFieldNames._entities, entityName, dataBaseRow);
					continue;
				} catch (Exception e) {
					entity.isPresentInThisUnionAll(unionAll, json);
					throw new RuntimeException(e);
				}
			}
			
			boolean willNotExecuteAction = false == specification.containsField(JsonFieldNames.action);
			
			if(willNotExecuteAction) {
			
				boolean willNotThrowException = false == specification.containsField(JsonFieldNames.status);
				
				if(willNotThrowException) {
					continue;
				}
				CcpProcessStatus status = specification.getAsObject(JsonFieldNames.status);
				String message = specification.getOrDefault(JsonFieldNames.message, () -> status.name());
				CcpJsonRepresentation put = json.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.message, message)
						.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.status, status);
				CcpJsonRepresentation apply = whenFlowError.apply(put);
				List<CcpJsonRepresentation> asList = Arrays.asList(specifications).stream()
						.map(j -> j.getTransformedJsonIfFoundTheField(JsonFieldNames.entity, FunctionPutEntity.INSTANCE))
						.map(j -> j.getTransformedJsonIfFoundTheField(JsonFieldNames.status, FunctionPutStatus.INSTANCE))
						.collect(Collectors.toList());
				CcpJsonRepresentation result = apply.put(JsonFieldNames.flow, asList);
				
				String reason = "Context: " + origin + ". Entity: " + entityDetails.entityName
				+ ". status: " + status
				+ ". shouldHaveBeenFound: " + shouldHaveBeenFound + ". wasActuallyFound: " + wasActuallyFound;
				
				throw new CcpErrorFlowDisturb(result, status , reason, this.fields);
			}
			
			CcpBusiness action = specification.getAsObject(JsonFieldNames.action);

			if(false == shouldHaveBeenFound) {
				json = action.apply(json);
				continue;
			}
			String entityName = entity.getEntityDetails().entityName;
			CcpJsonRepresentation dataBaseRow = entity.getRecordFromUnionAll(unionAll, json);
			CcpJsonRepresentation context = json.addToItem(JsonFieldNames._entities, entityName, dataBaseRow);
			json = action.apply(context);
		} 
		
		boolean zeroFields = this.fields.length <= 0;
		
		if(zeroFields) {
			throw new CcpErrorFlowFieldsToReturnNotMentioned(origin);
		}
		CcpJsonRepresentation apply = whenFlowSuccess.apply(json);
		CcpJsonRepresentation subMap = apply.getDynamicVersion().getJsonPiece(this.fields).put(JsonFieldNames.origin, origin);
		return subMap;
	}
}
