package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.db.crud.CcpErrorFlowFieldsToReturnNotMentioned;
import com.ccp.exceptions.process.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatus;


public class CcpSelectFinally {
	private final CcpJsonRepresentation id;
	private final CcpJsonRepresentation statements;
	private final String[] fields;
	CcpSelectFinally(CcpJsonRepresentation id, CcpJsonRepresentation statements, String[] fields) {
		this.id = id;
		this.fields = fields;
		this.statements = statements;

	}

	public CcpSelectFinally endThisProcedure(String context, Function<CcpJsonRepresentation, CcpJsonRepresentation> whenFlowError, Consumer<String[]> functionToDeleteKeysInTheCache) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList("statements");
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
		this.findById(context, this.id, whenFlowError, functionToDeleteKeysInTheCache, array);
		return this;
	}

	public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(String context, Function<CcpJsonRepresentation, CcpJsonRepresentation> whenFlowError, Consumer<String[]> functionToDeleteKeysInTheCache
			) {
		List<CcpJsonRepresentation> statements = this.statements.getAsJsonList("statements");
		CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
		CcpJsonRepresentation findById = this.findById(context, this.id, whenFlowError, functionToDeleteKeysInTheCache, array);
		return findById;
	}

	
	@SuppressWarnings("unchecked")
	private CcpJsonRepresentation findById( 
			String origin,
			CcpJsonRepresentation json, Function<CcpJsonRepresentation, 
			CcpJsonRepresentation> whenFlowError, 
			Consumer<String[]> functionToDeleteKeysInTheCache, 
			CcpJsonRepresentation... specifications) {
		List<CcpEntity> keySet = Arrays.asList(specifications).stream()
				.filter(x -> x.containsAllFields("entity"))
				.map(x -> (CcpEntity) x.get("entity") ) 
				.collect(Collectors.toList());
		
		LinkedHashSet<CcpEntity> set = new LinkedHashSet<>(keySet);

		CcpEntity[] entities = set.toArray(new CcpEntity[set.size()]);
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll unionAll = crud.unionAll(json, functionToDeleteKeysInTheCache, entities);
		
		for (CcpJsonRepresentation specification : specifications) {
			
			boolean executeFreeAction = specification.containsField("entity") == false;
			
			if(executeFreeAction) {
				Function<CcpJsonRepresentation, CcpJsonRepresentation> action = specification.getAsObject("action");
				json = action.apply(json);
				continue;
			}
			
			boolean shouldHaveBeenFound = specification.getAsBoolean("found");
			
			CcpEntity entity = specification.getAsObject("entity");
			
			boolean wasActuallyFound;
			
			try {
				wasActuallyFound = entity.isPresentInThisUnionAll(unionAll, json);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
			
			if(itWasNotForeseen) {

				if(wasActuallyFound == false) {
					continue;
				}
				String entityName = entity.getEntityName();
				try {
					CcpJsonRepresentation dataBaseRow = entity.getRequiredEntityRow(unionAll, json);
					json = json.addToItem("_entities", entityName, dataBaseRow);
					continue;
				} catch (Exception e) {
					entity.isPresentInThisUnionAll(unionAll, json);
					throw new RuntimeException(e);
				}
			}
			
			boolean willNotExecuteAction = specification.containsField("action") == false;
			
			if(willNotExecuteAction) {
			
				boolean willNotThrowException = specification.containsField("status") == false;
				
				if(willNotThrowException) {
					continue;
				}
				CcpProcessStatus status = specification.getAsObject("status");
				String message = specification.getOrDefault("message", status.name());
				CcpJsonRepresentation put = json.addToItem("errorDetails", "message", message).addToItem("errorDetails", "status", status);
				CcpJsonRepresentation apply = whenFlowError.apply(put);
				List<CcpJsonRepresentation> asList = Arrays.asList(specifications).stream()
						.map(j -> j.getTransformedJsonIfFoundTheField("entity", FunctionPutEntity.INSTANCE))
						.map(j -> j.getTransformedJsonIfFoundTheField("status", FunctionPutStatus.INSTANCE))
						.collect(Collectors.toList());
				CcpJsonRepresentation result = apply.put("flow", asList);
				
				String reason = "Context: " + origin + ". Entity: " + entity.getEntityName()
				+ ". status: " + status
				+ ". shouldHaveBeenFound: " + shouldHaveBeenFound + ". wasActuallyFound: " + wasActuallyFound;
				
				throw new CcpErrorFlowDisturb(result, status , reason, this.fields);
			}
			
			Function<CcpJsonRepresentation, CcpJsonRepresentation> action = specification.getAsObject("action");

			if(shouldHaveBeenFound == false) {
				json = action.apply(json);
				continue;
			}
			String entityName = entity.getEntityName();
			CcpJsonRepresentation dataBaseRow = entity.getRequiredEntityRow(unionAll, json);
			CcpJsonRepresentation context = json.addToItem("_entities", entityName, dataBaseRow);
			json = action.apply(context);
		} 
		
		boolean zeroFields = this.fields.length <= 0;
		
		if(zeroFields) {
			throw new CcpErrorFlowFieldsToReturnNotMentioned(origin);
		}
		
		CcpJsonRepresentation subMap = json.getJsonPiece(this.fields).put("origin", origin);
		return subMap;
	}
}
