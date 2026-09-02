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
		CcpJsonRepresentation executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			CcpJsonRepresentation result = entity.transferDataTo(json, entities);
			return result;
		}
	}, 
	copyDataTo{
		CcpJsonRepresentation executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			CcpJsonRepresentation result = entity.copyDataTo(json, entities);
			return result;
		}
	}, 
;
	abstract CcpJsonRepresentation executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer);
	
	/**
	 * Executa os fluxos {@code before}/{@code after} e a transferência entre entidades.
	 * @param json o JSON de entrada
	 * @param clazz a classe com as anotações {@code @CcpEntityDataTransfers}
	 * @param entity a entidade origem
	 * @param entityToTransfer a entidade destino
	 */
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationStepType._before, clazz, entity, entityToTransfer);
		CcpJsonRepresentation result = this.executeEntityTransfer(before, entity, entityToTransfer);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityOperationStepType._after, clazz, entity, entityToTransfer);
		return after;

	}
	
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationStepType when, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		
		CcpEntityDataTransfers annotation = clazz.getAnnotation(CcpEntityDataTransfers.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityDataTransfer[] transfers = annotation.transfers();
		
		for (CcpEntityDataTransfer transfer : transfers) {

			CcpEntityDecoratorTransferType operationType = transfer.transferType();
			boolean operationTypeEquals = operationType.equals(this);
			boolean valorIgual = false == operationTypeEquals;

			if(valorIgual) {
				continue;
			}

			CcpEntityMetaData entityToTransferEntityDetails = entityToTransfer.getEntityMetaData();
			Class<?> obj = transfer.to();
			boolean configurationClassEquals = entityToTransferEntityDetails.configurationClass.equals(obj);
			boolean wrongEntity = false == configurationClassEquals;
			
			if(wrongEntity) {
				continue;
			}
			
			CcpEntityType entityType = transfer.from();
			String extractEntityName = entityType.extractEntityName(clazz);
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			boolean extractEntityNameEquals = extractEntityName.equals(entityDetails.entityName);

			boolean isNotTheEntity = false == extractEntityNameEquals;
			
			if(isNotTheEntity) {
				continue;
			}
			var when2 = transfer.when();
			var when2Equals = when2.equals(when);

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
