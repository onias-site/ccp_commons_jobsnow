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
 * {@code @CcpEntityOperation}: {@code save}, {@code insert}, {@code update}, {@code delete} e
 * {@code deleteAnyWhere}. Cada constante implementa a operação sobre a entidade e expõe os fluxos
 * {@code before} e {@code after} em métodos separados ({@code executeBefore} e {@code executeAfter}),
 * porque cada fluxo é aplicado por um decorator próprio e em posição própria da cadeia.
 *
 * <p>{@code insert} e {@code update} são desfechos do {@code save}: {@code insert} quando o
 * {@code save} devolve {@code true} (o documento não existia) e {@code update} quando devolve
 * {@code false} (o documento já existia). O {@code save} configurado cobre os dois desfechos, ou seja,
 * é o que se usa quando o retorno do {@code save} não interessa.
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

		public boolean covers(CcpEntityDecoratorOperationType executed) {
			boolean isSave = save.equals(executed);
			boolean isInsert = insert.equals(executed);
			boolean isUpdate = update.equals(executed);
			boolean covers = isSave || isInsert || isUpdate;
			return covers;
		}

		public boolean isAfterFlowDispensed(boolean result) {
			return false;
		}

		public CcpEntityDecoratorOperationType getOutcome(boolean result) {
			if(result) {
				return insert;
			}
			return update;
		}
	},
	insert{
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = save.executeEntityOperation(json, entity);
			return result;
		}
	},
	update{
		boolean executeEntityOperation(CcpJsonRepresentation json, CcpEntity entity) {
			boolean result = save.executeEntityOperation(json, entity);
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
	 * Delega a operação ao restante da cadeia de decorators e executa o fluxo {@code after} do desfecho
	 * obtido. No {@code delete} o {@code after} é dispensado quando nenhum registro foi removido; no
	 * {@code save} ele sempre roda, e o desfecho ({@code insert} ou {@code update}) decide quais itens
	 * configurados disparam. Como a operação devolve apenas o resultado booleano, o fluxo {@code after}
	 * recebe o mesmo JSON que chegou a este decorator.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityOperations}
	 * @param entity a entidade alvo da operação
	 */
	public boolean executeAfter(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity) {
		boolean result = this.executeEntityOperation(json, entity);

		boolean afterFlowIsDispensed = this.isAfterFlowDispensed(result);

		if(afterFlowIsDispensed) {
			return result;
		}

		CcpEntityDecoratorOperationType outcome = this.getOutcome(result);
		outcome.executeFlow(json, CcpEntityOperationPhase._after, clazz, entity);
		return result;
	}

	/**
	 * Informa se o item configurado com esta operação dispara quando a operação {@code executed}
	 * acontece. Por padrão, só a própria operação; o {@code save} cobre também {@code insert} e
	 * {@code update}.
	 * @param executed a operação (ou o desfecho dela) que de fato aconteceu
	 */
	public boolean covers(CcpEntityDecoratorOperationType executed) {
		boolean covers = this.equals(executed);
		return covers;
	}

	/**
	 * Informa se o fluxo {@code after} deve ser dispensado dado o retorno da operação. Por padrão é
	 * dispensado quando a operação devolve {@code false} (o {@code delete} não encontrou registro); o
	 * {@code save} nunca dispensa, porque o {@code false} dele significa {@code update}.
	 * @param result o retorno da operação
	 */
	public boolean isAfterFlowDispensed(boolean result) {
		boolean operationDidNotHappen = false == result;
		return operationDidNotHappen;
	}

	/**
	 * Traduz o retorno da operação no desfecho que casa os itens do fluxo {@code after}. Por padrão o
	 * desfecho é a própria operação; no {@code save} é {@code insert} ({@code true}) ou {@code update}
	 * ({@code false}).
	 * @param result o retorno da operação
	 */
	public CcpEntityDecoratorOperationType getOutcome(boolean result) {
		return this;
	}

	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationPhase when, Class<?> clazz, CcpEntity entity) {
		
		CcpEntityOperations annotation = clazz.getAnnotation(CcpEntityOperations.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityOperation[] operations = annotation.value();

		for (CcpEntityOperation operation : operations) {

			CcpEntityOperationType configuredOperationType = operation.operationType();

			CcpEntityDecoratorOperationType operationType = configuredOperationType.operationType;
			boolean operationTypeCovers = operationType.covers(this);
			boolean valorIgual = false == operationTypeCovers;

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
