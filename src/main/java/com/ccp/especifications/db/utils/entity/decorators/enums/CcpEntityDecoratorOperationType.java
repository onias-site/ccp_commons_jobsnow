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
 * implementa a operação sobre a entidade e expõe os fluxos {@code before} e {@code after} em métodos
 * separados ({@code executeBefore} e {@code executeAfter}), porque cada fluxo é aplicado por um
 * decorator próprio e em posição própria da cadeia.
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
	 * Executa o fluxo {@code before} e, na sequência, delega a operação ao restante da cadeia de
	 * decorators. O JSON produzido pelo fluxo {@code before} é o que segue para os decorators
	 * internos, de modo que tudo o que vem depois enxerga o resultado dos side effects prévios.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityOperations}
	 * @param entity a entidade alvo da operação
	 */
	public boolean executeBefore(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationPhase._before, clazz, entity);
		boolean result = this.executeEntityOperation(before, entity);
		return result;
	}

	/**
	 * Delega a operação ao restante da cadeia de decorators e executa o fluxo {@code after} somente se
	 * a operação tiver acontecido de fato. Ou seja, o {@code after} é dispensado quando o {@code save}
	 * apenas atualizou um documento que já existia ou quando o {@code delete} não encontrou registro
	 * para remover. Como a operação devolve apenas o resultado booleano, o fluxo {@code after} recebe o
	 * mesmo JSON que chegou a este decorator.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityOperations}
	 * @param entity a entidade alvo da operação
	 */
	public boolean executeAfter(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity) {
		boolean result = this.executeEntityOperation(json, entity);

		boolean operationDidNotHappen = false == result;

		if(operationDidNotHappen) {
			return false;
		}

		this.executeFlow(json, CcpEntityOperationPhase._after, clazz, entity);
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
