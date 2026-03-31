package com.ccp.especifications.db.utils.entity.decorators.engine;

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
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityStepType;

class DecoratorOperationsWriterEntity extends CcpEntityDelegator {
	
	private final Class<?> clazz;
	
	public DecoratorOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityStepType.beforeDelete);
		CcpJsonRepresentation result = this.entity.delete(before);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityStepType.afterDelete);
		return after;
	}

	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityStepType.beforeDeletePermanently);
		CcpJsonRepresentation result = this.entity.deleteAnyWhere(before);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityStepType.afterDeletePermanently);
		return after;
	}
	
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityStepType.beforeSave);
		CcpJsonRepresentation result = this.entity.save(before);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityStepType.afterSave);
		return after;
	}
	
	private CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityStepType when) {
		
		CcpEntityOperations annotation = this.clazz.getAnnotation(CcpEntityOperations.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		
		CcpEntityOperation[] operations = annotation.operations();
		
		for (CcpEntityOperation operation : operations) {

			var globalExceptionHandlers = this.getExceptionHandlers(globalHandlers);
			
			String extractEntityName = operation.entityType().extractEntityName(this.clazz);
			CcpEntityDetails entityDetails = this.getEntityDetails();
			
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
	
	private CcpJsonRepresentation executeBusiness(CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
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

	private Map<Class<?>, List<CcpBusiness>> getExceptionHandlers(CcpExceptionFlow[] flows){
		
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
	
	private CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

}
