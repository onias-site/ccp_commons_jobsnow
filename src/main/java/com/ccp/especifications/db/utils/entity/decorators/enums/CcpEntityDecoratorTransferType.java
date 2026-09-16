package com.ccp.especifications.db.utils.entity.decorators.enums;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfers;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Define os tipos de transferência de dados entre entidades configurados via
 * {@code @CcpEntityDataTransfer}: {@code transferDataTo} (move e remove a origem) e
 * {@code copyDataTo} (copia sem remover a origem). Executa os fluxos {@code before}/{@code after}
 * configurados na anotação.
 */
public enum CcpEntityDecoratorTransferType implements OperationWriter{
	transferDataTo{
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.transferDataTo(json, entities);
			return result;
		}
	},
	copyDataTo{
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.copyDataTo(json, entities);
			return result;
		}
	},
;
	abstract boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer);

	/**
	 * Executa o fluxo {@code before}, a transferência entre entidades e, somente se a transferência
	 * tiver acontecido de fato, o fluxo {@code after}. Ou seja, o {@code after} é dispensado quando não
	 * havia registro de origem para transferir ou copiar. Como a transferência devolve apenas o
	 * resultado booleano, o fluxo {@code after} recebe o JSON produzido pelo fluxo {@code before}.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityDataTransfers}
	 * @param entity a entidade origem
	 * @param entityToTransfer a entidade destino
	 */
	public boolean execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationPhase._before, clazz, entity, entityToTransfer);
		boolean result = this.executeEntityTransfer(before, entity, entityToTransfer);

		boolean transferDidNotHappen = false == result;

		if(transferDidNotHappen) {
			return false;
		}

		this.executeFlow(before, CcpEntityOperationPhase._after, clazz, entity, entityToTransfer);
		return result;

	}
	
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationPhase when, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		
		CcpEntityDataTransfers annotation = clazz.getAnnotation(CcpEntityDataTransfers.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityDataTransfer[] transfers = annotation.value();

		for (CcpEntityDataTransfer transfer : transfers) {

			CcpEntityDataTransferType configuredTransferType = transfer.operationType();
			{
				CcpEntityDecoratorTransferType operationType = configuredTransferType.transferType;
				boolean operationTypeEquals = operationType.equals(this);
				boolean isNotOperationTyype = false == operationTypeEquals;
				
				if(isNotOperationTyype) {
					continue;
				}
				
			}
			{
				CcpEntityMetaData entityToTransferEntityDetails = entityToTransfer.getEntityMetaData();
				Class<?> targetEntity = transfer.targetEntity();
				boolean configurationClassEquals = entityToTransferEntityDetails.configurationClass.equals(targetEntity);
				boolean wrongTarget = false == configurationClassEquals;
				
				if(wrongTarget) {
					continue;
				}
			}
			
			{
				
				CcpEntityPhase entityPhase = configuredTransferType.entityPhase;
				String extractEntityName = entityPhase.extractEntityName(clazz);
				CcpEntityMetaData entityDetails = entity.getEntityMetaData();
				boolean extractEntityNameEquals = extractEntityName.equals(entityDetails.entityName);
				
				boolean wrongEntityPhase = false == extractEntityNameEquals;
				
				if(wrongEntityPhase) {
					continue;
				}
			}
			 
			CcpEntityOperationPhase when2 = configuredTransferType.operationPhase;
			
			boolean when2Equals = when2.equals(when);

			boolean whenNotFound = false == when2Equals;
			
			if(whenNotFound) {
				continue;
			}
			
			var localHandlers = transfer.transferHandlers();
			var localExceptionHandlers = this.getExceptionHandlers(localHandlers);
			var globalExceptionHandlers = this.getExceptionHandlers(globalHandlers);
			globalExceptionHandlers.putAll(localExceptionHandlers);
			Class<?>[] execute = transfer.execute();
			for (var businessClass : execute) {
				CcpBusiness business = this.getBusiness(businessClass);
				json = this.executeBusiness(json, business, globalExceptionHandlers);
			}
			return json;
		}
		return json;
	}
}
