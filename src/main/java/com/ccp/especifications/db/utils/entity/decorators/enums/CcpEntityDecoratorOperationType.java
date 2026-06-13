package com.ccp.especifications.db.utils.entity.decorators.enums;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOperations;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Define os tipos de operação com side effects que podem ser configurados para uma entidade via
 * {@code @CcpEntityOperation}: {@code save}, {@code delete} e {@code deleteAnyWhere}. Cada constante
 * implementa a operação sobre a entidade e executa os fluxos {@code before}/{@code after} configurados
 * na anotação.
 */
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
	
	/**
	 * Executa os fluxos {@code before}/{@code after} e a operação sobre a entidade informada.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityOperations}
	 * @param entity a entidade alvo da operação
	 */
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
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			
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
