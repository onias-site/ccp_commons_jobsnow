package com.ccp.especifications.db.utils.entity.decorators.enums;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfer;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityDataTransfers;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

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
	
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		CcpJsonRepresentation before = this.executeFlow(json, CcpEntityOperationStepType.before, clazz, entity, entityToTransfer);
		CcpJsonRepresentation result = this.executeEntityTransfer(before, entity, entityToTransfer);
		CcpJsonRepresentation after = this.executeFlow(result, CcpEntityOperationStepType.after, clazz, entity, entityToTransfer);
		return after;

	}
	
	protected CcpJsonRepresentation executeFlow(CcpJsonRepresentation json, CcpEntityOperationStepType when, Class<?> clazz, CcpEntity entity, CcpEntity entityToTransfer) {
		
		CcpEntityDataTransfers annotation = clazz.getAnnotation(CcpEntityDataTransfers.class);
		
		CcpExceptionFlow[] globalHandlers = annotation.globalHandlers();
		
		CcpEntityDataTransfer[] transfers = annotation.transfers();
		
		for (CcpEntityDataTransfer transfer : transfers) {

			CcpEntityDecoratorTransferType operationType = transfer.transferType();
			
			if(false == operationType.equals(this)) {
				continue;
			}

			CcpEntityDetails entityToTransferEntityDetails = entityToTransfer.getEntityDetails();
			Class<?> obj = transfer.to();
			boolean wrongEntity = false == entityToTransferEntityDetails.configurationClass.equals(obj);
			
			if(wrongEntity) {
				continue;
			}
			
			CcpEntityType entityType = transfer.from();
			String extractEntityName = entityType.extractEntityName(clazz);
			CcpEntityDetails entityDetails = entity.getEntityDetails();
			
			boolean isNotTheEntity = false == extractEntityName.equals(entityDetails.entityName);
			
			if(isNotTheEntity) {
				continue;
			}

			boolean whenNotFound = false == transfer.when().equals(when);
			
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
