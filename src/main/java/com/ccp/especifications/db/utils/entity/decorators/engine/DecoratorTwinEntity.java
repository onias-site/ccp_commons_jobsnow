package com.ccp.especifications.db.utils.entity.decorators.engine;

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
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

class DecoratorTwinEntity extends CcpDefaultEntityDelegator<CcpEntityTwin>{
	
	private final CcpEntity twin;
	private final Class<?>  clazz;

	public DecoratorTwinEntity(CcpEntity entity, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.twin = CcpEntityFactory.getEntity(clazz, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
		this.clazz = clazz;
	}

	private DecoratorTwinEntity(CcpEntity entity, Class<?> clazz, CcpEntity twin) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.clazz = clazz;
		this.twin = twin;
	}
	
	private static Consumer<String[]> instanciateFunctionToDeleteKeysInTheCache(Class<?> clazz) {
		CcpEntityTwin annotation = clazz.getAnnotation(CcpEntityTwin.class);
		Class<?> clz = annotation.functionToDeleteKeysInTheCacheClass();
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clz);
		Consumer<String[]> newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}

	private static CcpExecuteBulkOperation instanciateBulkExecutor(Class<?> clazz) {
		CcpEntityTwin annotation = clazz.getAnnotation(CcpEntityTwin.class);
		Class<?> clz = annotation.bulkExecutorClass();
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clz);
		CcpExecuteBulkOperation newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}


	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		var transfer = new CcpEntityBulkHandlerTransferRecordToReverseEntity(this);
		super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		return json;
	}
	
	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		ArrayList<CcpEntity> result = new ArrayList<CcpEntity>(associatedEntities);
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
	
	public CcpEntity getTwinEntity() {
		DecoratorTwinEntity twin = new DecoratorTwinEntity(this.twin, this.clazz, this);
		return twin;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		var transfer = new CcpEntityBulkHandlerSaveTwinEntity(this);
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
