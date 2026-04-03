package com.ccp.especifications.db.utils.entity.decorators.enums;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public enum CcpEntityDecoratorOperationType implements OperationWriter{
	deleteAnyWhere{

		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			CcpJsonRepresentation result = entity.deleteAnyWhere(json);
			return result;
		}
	},
	delete{
		
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			CcpJsonRepresentation result = entity.delete(json);
			return result;
		}
	},
	save{
		CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			CcpJsonRepresentation result = entity.save(json);
			return result;
		}
	},
;
	abstract CcpJsonRepresentation executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity);
	
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity... entities) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationStepType.before, clazz, entity);
		CcpJsonRepresentation result = this.executeEntityOperation(before, entity);
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
			
			CcpEntityType entityType = operation.from();
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
}
