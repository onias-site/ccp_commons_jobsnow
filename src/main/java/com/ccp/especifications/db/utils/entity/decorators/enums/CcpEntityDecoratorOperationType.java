package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.List;
import java.util.Map;

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

		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.deleteAnyWhere(json);
			return result;
		}
	},
	delete{

		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.delete(json);
			return result;
		}
	},
	save{
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = entity.save(json);
			return result;
		}
	},
;
	abstract boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity);

	/**
	 * Executa o fluxo {@code before}, a operação sobre a entidade informada e, somente se a operação
	 * tiver acontecido de fato, o fluxo {@code after}. Ou seja, o {@code after} é dispensado quando o
	 * {@code save} apenas atualizou um documento que já existia ou quando o {@code delete} não
	 * encontrou registro para remover. Como a operação devolve apenas o resultado booleano, o fluxo
	 * {@code after} recebe o JSON produzido pelo fluxo {@code before}.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityOperations}
	 * @param entity a entidade alvo da operação
	 */
	public boolean execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity... entities) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationPhase._before, clazz, entity);
		boolean result = this.executeEntityOperation(before, entity);

		boolean operationDidNotHappen = false == result;

		if(operationDidNotHappen) {
			return false;
		}

		this.executeFlow(before, CcpEntityOperationPhase._after, clazz, entity);
		return result;
	}
	
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationPhase when, Class<?> clazz, CcpEntity entity) {
		
		CcpEntityOperations annotation = clazz.getAnnotation(CcpEntityOperations.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityOperation[] operations = annotation.value();

		for (CcpEntityOperation operation : operations) {

			CcpEntityOperationType configuredOperationType = operation.operationType();

			CcpEntityDecoratorOperationType operationType = configuredOperationType.operationType;
			boolean operationTypeEquals = operationType.equals(this);
			boolean valorIgual = false == operationTypeEquals;

			if(valorIgual) {
				continue;
			}

			CcpEntityPhase entityType = configuredOperationType.entityPhase;
			String extractEntityName = entityType.extractEntityName(clazz);
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			boolean extractEntityNameEquals = extractEntityName.equals(entityDetails.entityName);

			boolean isNotTheEntity = false == extractEntityNameEquals;
			
			if(isNotTheEntity) {
				continue;
			}
			var when2 = configuredOperationType.operationPhase;
			var when2Equals = when2.equals(when);

			boolean whenNotFound = false == when2Equals;
			
			if(whenNotFound) {
				continue;
			}
			
			CcpExceptionFlow[] localHandlers = operation.operationHandlers();
			Map<Class<?>, List<CcpBusiness>> localExceptionHandlers = this.getExceptionHandlers(localHandlers);
			Map<Class<?>, List<CcpBusiness>> globalExceptionHandlers = this.getExceptionHandlers(globalHandlers);
			globalExceptionHandlers.putAll(localExceptionHandlers);
			Class<?>[] execute = operation.execute();
			for (Class<?> businessClass : execute) { 
				CcpBusiness business = this.getBusiness(businessClass);
				json = this.executeBusiness(json, business, globalExceptionHandlers);
			}
			return json;
		} 
		
		return json;
	}
	
}
