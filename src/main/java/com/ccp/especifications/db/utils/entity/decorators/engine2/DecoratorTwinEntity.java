package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerSaveTwinEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityTransferType;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

class DecoratorTwinEntity extends CcpDefaultEntityDelegator<CcpEntityTwin>{
	
	private final CcpEntity2 twin;
	private final Class<?>  clazz;

	public DecoratorTwinEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.twin = CcpEntityFactory.getEntity(clazz, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
		this.clazz = clazz;
	}

	private DecoratorTwinEntity(CcpEntity2 entity, Class<?> clazz, CcpEntity2 twin) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.clazz = clazz;
		this.twin = twin;
	}
	
	private static Consumer<String[]> instanciateFunctionToDeleteKeysInTheCache(Class<?> functionToDeleteKeysInTheCacheClass) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(functionToDeleteKeysInTheCacheClass);
		Consumer<String[]> newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

	private static CcpExecuteBulkOperation instanciateBulkExecutor(Class<?> bulkExecutorClass) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(bulkExecutorClass);
		CcpExecuteBulkOperation newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}


	private CcpEntityTransferType getTransferType() {
		CcpEntityTwin annotation = this.clazz.getAnnotation(CcpEntityTwin.class);
		String twinEntityName = annotation.twinEntityName();
		CcpEntityDetails entityDetails = this.getEntityDetails();
		CcpEntityTransferType transferType = entityDetails.entityName.equals(twinEntityName) ? CcpEntityTransferType.Reactivate : CcpEntityTransferType.Inactivate;
		return transferType;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpEntityTransferType transferType = this.getTransferType();
		var transfer = new CcpEntityBulkHandlerTransferRecordToReverseEntity(transferType, this.clazz);
		super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		return json;
	}
	
	public List<CcpEntity2> getAssociatedEntities() {
		List<CcpEntity2> associatedEntities = this.entity.getAssociatedEntities();
		ArrayList<CcpEntity2> result = new ArrayList<CcpEntity2>(associatedEntities);
		result.add(this.twin);
		return result;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation oneByIdAnyWhere = super.getOneByIdAnyWhere(json);
		CcpDynamicJsonRepresentation dynamicVersion = oneByIdAnyWhere.getDynamicVersion();
		{
			CcpEntityDetails entityDetails = this.getEntityDetails();
			boolean foundInMainEntity = dynamicVersion.containsAllFields(entityDetails.entityName);
			
			if(foundInMainEntity) {
				CcpJsonRepresentation innerJson = dynamicVersion.getInnerJson(entityDetails.entityName);
				return innerJson;
			}
		}
		CcpEntityDetails entityDetails = this.twin.getEntityDetails();
		boolean foundInTwinEntity = dynamicVersion.containsAllFields(entityDetails.entityName);
		
		if(foundInTwinEntity) {
			String id = this.twin.calculateId(json);
			String errorMessage = String.format("The id '%s' has been moved from '%s' to '%s' ", id, this,  this.twin);
			throw new CcpErrorFlowDisturb(json, CcpProcessStatusDefault.REDIRECT, errorMessage, new String[0]);
		}

		CcpJsonRepresentation oneById =  this.entity.getOneById(json);
		return oneById;
	}
	
	public CcpEntity2 getTwinEntity() {
		DecoratorTwinEntity twin = new DecoratorTwinEntity(this.twin, this.clazz, this);
		return twin;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpEntityTransferType transferType = this.getTransferType();
		var transfer = new CcpEntityBulkHandlerSaveTwinEntity(transferType, this.clazz);
		super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		return json;
	}
	
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		ArrayList<CcpBulkItem> items = new ArrayList<>(bulkItems);
		var expurgableToBulkOperation = this.twin.toBulkItems(json, operation);
		items.addAll(expurgableToBulkOperation);
		return items;
	}

}
