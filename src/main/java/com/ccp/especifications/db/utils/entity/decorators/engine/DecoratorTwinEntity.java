package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpDynamicJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerDelete;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerSaveTwinEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToTwinEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

class DecoratorTwinEntity extends CcpDefaultEntityDelegator<CcpEntityTwin>{
	
	private CcpEntity twin;
	
	private final Class<?> clazz;
	
	public DecoratorTwinEntity(CcpEntity entity, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.clazz = clazz;
	}

	private DecoratorTwinEntity(CcpEntity entity, CcpEntity twin, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.twin = twin;
		this.clazz = clazz;
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
		var transfer = new CcpEntityBulkHandlerTransferRecordToTwinEntity(this);
		super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		return json;
	}
	
	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		ArrayList<CcpEntity> result = new ArrayList<CcpEntity>(associatedEntities);
		CcpEntity wrapedTwinEntity = this.getWrapedTwinEntity();
		List<CcpEntity> associatedEntities2 = wrapedTwinEntity.getAssociatedEntities();
		result.addAll(associatedEntities2);
		ArrayList<CcpEntity> list = new ArrayList<>(new HashSet<>(result));
		return list;
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
		CcpEntityDetails entityDetails = this.getTwinEntity().getEntityDetails();
		boolean foundInTwinEntity = dynamicVersion.containsAllFields(entityDetails.entityName);
		
		if(foundInTwinEntity) {
			String id = this.getTwinEntity().calculateId(json);
			String errorMessage = String.format("The id '%s' has been moved from '%s' to '%s' ", id, this,  this.getTwinEntity());
			throw new CcpErrorFlowDisturb(json, CcpProcessStatusDefault.REDIRECT, errorMessage, new String[0]);
		} 

		CcpJsonRepresentation oneById =  this.entity.getOneById(json);
		return oneById;
	}
	
	public CcpEntity getTwinEntity() {
		
		if(this.twin != null) {
			return this.twin;
		}
		
		String twinEntityName = this.clazz.getAnnotation(CcpEntityTwin.class).twinEntityName();
		CcpEntityDetails entityDetails = this.entity.getEntityDetails();

		boolean isNotTwin = false == entityDetails.entityName.equals(twinEntityName);
		
		if(isNotTwin) {
			this.twin = CcpEntityFactory.getEntity(this.clazz, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
			return this.twin;
		}
		
		try {
			CcpEntityConfigurator cfg = new CcpReflectionConstructorDecorator(this.clazz).newInstance();
			this.twin = cfg.getEntity();
			return this.twin;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpEntity twinEntity = this.getTwinEntity();
		var deleteTwinEntity = new CcpBulkHandlerDelete(twinEntity);
		var saveMainEntity = new CcpEntityBulkHandlerSaveTwinEntity(this);
		super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, saveMainEntity, deleteTwinEntity);
		return json;
	}
	
	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch =  new ArrayList<CcpJsonRepresentation>(this.entity.getParametersToSearch(json));
		CcpEntity wrapedEntity = this.getWrapedTwinEntity();
		List<CcpJsonRepresentation> parametersToSearchTwin = wrapedEntity.getParametersToSearch(json);
		parametersToSearch.addAll(parametersToSearchTwin);
		return parametersToSearch;
	}
	
	private CcpEntity getWrapedTwinEntity() {
		CcpEntity twinEntity = this.getTwinEntity();
		CcpEntity wrapedEntity = twinEntity.getWrapedEntity();
		while(false == wrapedEntity instanceof DecoratorTwinEntity) {
			wrapedEntity = wrapedEntity.getWrapedEntity();
		}
		
		return wrapedEntity.getWrapedEntity();
	}
	
	
}
