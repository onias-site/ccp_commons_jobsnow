package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerDelete;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerSaveTwinEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToTwinEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Decorator que implementa o padrão de entidade twin para entidades anotadas com
 * {@code @CcpEntityTwin}. Gerencia a migração de registros entre o índice principal e o índice
 * twin: {@code save} grava na entidade principal e apaga do twin; {@code delete} transfere o
 * registro para o twin. Usa o executor bulk e a função de limpeza de cache configurados na anotação.
 */
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


	/**
	 * Move o registro para a entidade gêmea. O {@code unionAll} disparado antes do bulk diz se o
	 * registro existia, que é o que define se houve de fato uma remoção da entidade principal.
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(CcpJsonRepresentation json) {
		var transfer = new CcpEntityBulkHandlerTransferRecordToTwinEntity(this);
		CcpSelectUnionAll unionAll = super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		boolean existedBeforeTheDeletion = this.isPresentInThisUnionAll(unionAll, json);
		return existedBeforeTheDeletion;
	}

	//FIXME NAO ESTA PERMITINDO EXECUTAR ESTE METODO QUANDO A PK SOFRE LGPD
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		CcpEntity customEntity = CcpEntityFactory.getCustomEntity(this, CcpEntityDecoratorTypes.Twin);
		CcpEntity twinEntity = this.getTwinEntity(CcpEntityDecoratorTypes.Twin);
		boolean deletedFromMainEntity = customEntity.delete(json);
		boolean deletedFromTwinEntity = twinEntity.delete(json);
		boolean deleted = deletedFromMainEntity || deletedFromTwinEntity;
		return deleted;
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
		{
			boolean foundInMainEntity = oneByIdAnyWhere.containsAllFields(this);

			if(foundInMainEntity) {
				CcpJsonRepresentation innerJson = oneByIdAnyWhere.getInnerJson(this);
				return innerJson;
			}
		}
		boolean foundInTwinEntity = oneByIdAnyWhere.containsAllFields(this);
		
		if(foundInTwinEntity) {
			CcpEntity twinEntity2 = this.getTwinEntity();
			String id = twinEntity2.calculateId(json);
			CcpEntity twinEntity3 = this.getTwinEntity();
			String errorMessage = String.format("The id '%s' has been moved from '%s' to '%s' ", id, this,  twinEntity3);
			CcpErrorFlowDisturb ccpErrorFlowDisturb = new CcpErrorFlowDisturb(json, CcpProcessStatusDefault.REDIRECT, errorMessage, new CcpJsonFieldName[0]);
			throw ccpErrorFlowDisturb;
		} 

		CcpJsonRepresentation oneById =  this.entity.getOneById(json);
		return oneById;
	}
	
	public CcpEntity getTwinEntity(CcpEntityDecoratorTypes... decoratorsToAvoid) {
		boolean twinDiferente = this.twin != null;
	
		if(twinDiferente) {
			return this.twin;
		}
		
		String twinEntityName = this.clazz.getAnnotation(CcpEntityTwin.class).twinEntityName();
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		boolean entityNameEquals = entityDetails.entityName.equals(twinEntityName);

		boolean isNotTwin = false == entityNameEquals;
		
		if(isNotTwin) {
			this.twin = CcpEntityFactory.getEntity(this.clazz, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName(), decoratorsToAvoid);
			return this.twin;
		}
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator2 = new CcpReflectionConstructorDecorator(this.clazz);

		CcpEntityConfigurator cfg = ccpReflectionConstructorDecorator2.newInstance();
		this.twin = cfg.getEntity();
		return this.twin;
	}

	/**
	 * Grava na entidade principal e apaga da gêmea. O {@code unionAll} disparado antes do bulk diz se a
	 * entidade principal já tinha o registro, que é o que distingue a inclusão da atualização.
	 */
	@SuppressWarnings("unchecked")
	public boolean save(CcpJsonRepresentation json) {
		CcpEntity twinEntity = this.getTwinEntity();
		var deleteTwinEntity = new CcpBulkHandlerDelete(twinEntity);
		var saveMainEntity = new CcpEntityBulkHandlerSaveTwinEntity(this);
		CcpSelectUnionAll unionAll = super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, saveMainEntity, deleteTwinEntity);
		boolean alreadyExisted = this.isPresentInThisUnionAll(unionAll, json);
		boolean inserted = false == alreadyExisted;
		return inserted;
	}
	
	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch2 = this.entity.getParametersToSearch(json);
		List<CcpJsonRepresentation> parametersToSearch =  new ArrayList<CcpJsonRepresentation>(parametersToSearch2);
		CcpEntity wrapedEntity = this.getWrapedTwinEntity();
		List<CcpJsonRepresentation> parametersToSearchTwin = wrapedEntity.getParametersToSearch(json);
		parametersToSearch.addAll(parametersToSearchTwin);
		return parametersToSearch;
	}
	
	private CcpEntity getWrapedTwinEntity() {
		CcpEntity customEntity = CcpEntityFactory.getCustomEntity(this);
		CcpEntity twinEntity = customEntity.getTwinEntity();
		CcpEntity wrapedEntity = twinEntity.getWrapedEntity();
		while(false == wrapedEntity instanceof DecoratorTwinEntity) {
			wrapedEntity = wrapedEntity.getWrapedEntity();
			var wrapedEntityClass = wrapedEntity.getClass();
			CcpEntity wrapedEntity2 = wrapedEntity.getWrapedEntity();
			var wrapedEntity2Class = wrapedEntity2.getClass();
			boolean twinIsMissing = wrapedEntityClass.equals(wrapedEntity2Class);
			if(twinIsMissing) {
				CcpErrorEntityIsNotTwin ccpErrorEntityIsNotTwin = new CcpErrorEntityIsNotTwin(this.entity);
				throw ccpErrorEntityIsNotTwin;
			}
		}
		CcpEntity wrapedEntity3 = wrapedEntity.getWrapedEntity();

		return wrapedEntity3;
	}

	/**
	 * Exceção lançada quando se procura a entidade gêmea de uma entidade que não foi decorada como gêmea,
	 * ou seja, a cadeia de decoradores foi percorrida até o fim sem encontrar um {@code DecoratorTwinEntity}.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorEntityIsNotTwin extends RuntimeException {
		/**
		 * Monta a mensagem informando qual entidade não possui gêmea.
		 * @param entity a entidade que não é gêmea
		 */
		private CcpErrorEntityIsNotTwin(CcpEntity entity) {
			super(entity + " is not a twin entity");
		}
	}

}
