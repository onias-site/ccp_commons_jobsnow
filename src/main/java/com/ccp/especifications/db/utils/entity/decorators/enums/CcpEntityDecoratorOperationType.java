package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public enum CcpEntityDecoratorOperationType {
	deleteAnyWhere{

		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity... entities) {
			CcpJsonRepresentation result = entity.deleteAnyWhere(json);
			return result;
		}
		
	},
	transferDataTo{
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity... entities) {
			CcpJsonRepresentation result = entity.transferDataTo(json, entities);
			return result;
		}
		
	}, 
	copyDataTo{
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity... entities) {
			CcpJsonRepresentation result = entity.copyDataTo(json, entities);
			return result;
		}
		
	}, 
	delete{
		
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity... entities) {
			CcpJsonRepresentation result = entity.delete(json);
			return result;
		}
	},
	save{
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity... entities) {
			CcpJsonRepresentation result = entity.save(json);
			return result;
		}
	
	},
;
	abstract CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity, CcpEntity... entities);
	
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity... entities) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationStepType.before, clazz, entity);
		CcpJsonRepresentation result = this.executeEntityOperation(before, entity, entities);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityOperationStepType.after, clazz, entity);
		return after;

	}
	
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationStepType when, Class<?> clazz, CcpEntity entity) {
		
		CcpEntityOperations annotation = clazz.getAnnotation(CcpEntityOperations.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityOperation[] operations = annotation.operations();
		
		for (CcpEntityOperation operation : operations) {

			CcpEntityDecoratorOperationType operationType = operation.operation();
			
			if(false == operationType.equals(this)) {
				continue;
			}
			
			CcpEntityType entityType = operation.entityType();
			String extractEntityName = entityType.extractEntityName(clazz);
			CcpEntityDetails entityDetails = entity.getEntityDetails();
			
			boolean isNotTheEntity = false == extractEntityName.equals(entityDetails.entityName);
			
			if(isNotTheEntity) {
				continue;
			}

			boolean whenNotFound = false == operation.when().equals(when);
			
			if(whenNotFound) {
				continue;
			}
			
			var localHandlers = operation.operationHandlers();
			var localExceptionHandlers = this.getExceptionHandlers(localHandlers);
			var globalExceptionHandlers = this.getExceptionHandlers(globalHandlers);
			globalExceptionHandlers.putAll(localExceptionHandlers);
			Class<?>[] execute = operation.execute();
			for (var businessClass : execute) {
				CcpBusiness business = this.getBusiness(businessClass);
				json = this.executeBusiness(json, business, globalExceptionHandlers);
			}
			return json;
		}
		
		return json;
	}
	
	protected CcpJsonRepresentation executeBusiness(CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
		try {
			CcpJsonRepresentation apply = business.apply(json);
			return apply;
		} catch (RuntimeException e) {
			Class<? extends RuntimeException> clazz = e.getClass();
			List<CcpBusiness> list = exceptionHandlers.get(clazz);
			
			boolean notForeseen = list == null;
			
			if(notForeseen) {
				throw e;
			}
			
			for (CcpBusiness ccpBusiness : list) {
				json = this.executeBusiness(json, ccpBusiness, exceptionHandlers);
			}
			return json;
		}
	}

	protected Map<Class<?>, List<CcpBusiness>> getExceptionHandlers(CcpExceptionFlow[] flows){
		
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		
		for (CcpExceptionFlow flow : flows) {
			Class<?> whenThrowing = flow.whenThrowing();
			Class<?>[] thenExecute = flow.thenExecute();
			var asList = Arrays.asList(thenExecute)
					.stream()
					.map(x -> this.getBusiness(x))
					.collect(Collectors.toList())
					;
			
			result.put(whenThrowing, asList);
		}
		return result;
	}
	
	protected CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

}
