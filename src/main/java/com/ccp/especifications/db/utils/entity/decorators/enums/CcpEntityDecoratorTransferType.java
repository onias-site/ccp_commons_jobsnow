package com.ccp.especifications.db.utils.entity.decorators.enums;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfers;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * The kinds of data transfer between entities configured through {@code @CcpEntityDataTransfer}:
 * {@code transferDataTo} (moves and removes the source) and {@code copyDataTo} (copies without removing the source).
 * Exposes the {@code before} and {@code after} flows in separate methods ({@code executeBefore} and
 * {@code executeAfter}), because each flow is applied by its own decorator at its own position of the chain.
 */
public enum CcpEntityDecoratorTransferType implements OperationWriter{
	/** Moves the record to the target entity, removing it from the source. */
	transferDataTo{
		/**
		 * Runs {@code transferDataTo} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param entities the target entity
		 * @return whether the record existed in the source
		 */
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.transferDataTo(json, entities);
			return result;
		}
	},
	/** Copies the record to the target entity, keeping it in the source. */
	copyDataTo{
		/**
		 * Runs {@code copyDataTo} on the rest of the chain.
		 * @param json the record
		 * @param entity the rest of the chain
		 * @param entities the target entity
		 * @return whether the record existed in the source
		 */
		boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, 	CcpEntity entities) {
			boolean result = entity.copyDataTo(json, entities);
			return result;
		}
	},
;
	/**
	 * Performs the transfer on the rest of the decorator chain.
	 * @param json the record
	 * @param entity the rest of the chain
	 * @param entityToTransfer the target entity
	 * @return whether the record existed in the source
	 */
	abstract boolean executeEntityTransfer(CcpJsonRepresentation json, CcpEntity entity, CcpEntity entityToTransfer);

	/**
	 * Runs the {@code before} flow and then delegates the transfer to the rest of the decorator chain. The JSON produced by
	 * the {@code before} flow is the one that goes to the inner decorators. Unlike the write operations, a handled
	 * exception does not cancel the transfer.
	 * @param json the input JSON
	 * @param clazz the class with the {@code @CcpEntityDataTransfers} annotation
	 * @param entity the source entity
	 * @param entityToTransfer the target entity
	 * @return the result of the transfer
	 */
	public boolean executeBefore(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationPhase._before, clazz, entity, entityToTransfer);
		boolean result = this.executeEntityTransfer(before, entity, entityToTransfer);
		return result;
	}

	/**
	 * Delegates the transfer to the rest of the decorator chain and runs the {@code after} flow only when the transfer
	 * actually happened (there was a source record). Since the transfer returns only a boolean, the {@code after} flow
	 * receives the same JSON that reached this decorator.
	 * @param json the input JSON
	 * @param clazz the class with the {@code @CcpEntityDataTransfers} annotation
	 * @param entity the source entity
	 * @param entityToTransfer the target entity
	 * @return the result of the transfer
	 */
	public boolean executeAfter(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		boolean result = this.executeEntityTransfer(json, entity, entityToTransfer);

		boolean transferDidNotHappen = false == result;

		if(transferDidNotHappen) {
			return false;
		}

		this.executeFlow(json, CcpEntityOperationPhase._after, clazz, entity, entityToTransfer);
		return result;
	}

	/**
	 * Runs the businesses of the first {@code @CcpEntityDataTransfer} whose transfer type is this one, whose target entity
	 * is the given one, whose entity phase names the source entity and whose phase is {@code when}, chaining their outputs.
	 * Only the first matching item runs. Local exception handlers take precedence over the global ones.
	 * @param json the input JSON
	 * @param when the phase to run
	 * @param clazz the class with the {@code @CcpEntityDataTransfers} annotation
	 * @param entity the source entity
	 * @param entityToTransfer the target entity
	 * @return the JSON produced by the businesses, or the input JSON when no item matched
	 */
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
