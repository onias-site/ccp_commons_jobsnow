package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerDelete;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerSaveTwinEntity;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToTwinEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Decorator that implements the twin entity pattern for entities annotated with
 * {@code @CcpEntityTwin}. Manages the migration of records between the main index and the twin
 * index: {@code save} writes to the main entity and deletes from the twin; {@code delete} transfers the
 * record to the twin. Uses the bulk executor and the cache cleanup function configured in the annotation.
 */
class DecoratorTwinEntity extends CcpDefaultEntityDelegator<CcpEntityTwin>{
	
	private final Class<?> clazz;

	private CcpEntity twin;
	
	
	public DecoratorTwinEntity(CcpEntity entity, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.clazz = clazz; 
	}

	private DecoratorTwinEntity(CcpEntity entity, CcpEntity twin, Class<?> clazz) {
		super(entity, instanciateBulkExecutor(clazz), instanciateFunctionToDeleteKeysInTheCache(clazz));
		this.clazz = clazz;
		this.twin = twin;
	}

	private static Consumer<String[]> instanciateFunctionToDeleteKeysInTheCache(Class<?> clazz) {
		CcpEntityTwin annotation = clazz.getAnnotation(CcpEntityTwin.class);
		Class<?> cacheCleanerClass = annotation.functionToDeleteKeysInTheCacheClass();
		CcpReflectionConstructorDecorator cacheCleanerConstructor = new CcpReflectionConstructorDecorator(cacheCleanerClass);
		Consumer<String[]> cacheCleaner = cacheCleanerConstructor.newInstance();
		return cacheCleaner;
	}

	private static CcpExecuteBulkOperation instanciateBulkExecutor(Class<?> clazz) {
		CcpEntityTwin annotation = clazz.getAnnotation(CcpEntityTwin.class);
		Class<?> bulkExecutorClass = annotation.bulkExecutorClass();
		CcpReflectionConstructorDecorator bulkExecutorConstructor = new CcpReflectionConstructorDecorator(bulkExecutorClass);
		CcpExecuteBulkOperation bulkExecutor = bulkExecutorConstructor.newInstance();
		return bulkExecutor;
	}


	/**
	 * Moves the record to the twin entity. The {@code unionAll} triggered before the bulk tells whether the
	 * record existed, which is what defines whether there was actually a removal from the main entity.
	 *
	 * <p>A missing record is not an error: it produces no operation at all and the return value is {@code false}, as
	 * {@code CcpEntity.delete} promises. Throwing {@code NOT_FOUND} here made the
	 * "nothing was deleted" branch of whoever decorates {@code delete} from the outside unreachable.
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(CcpJsonRepresentation json) {
		Function<CcpBulkItem, List<CcpBulkItem>> nothingToTransfer = item -> new ArrayList<>();
		var transfer = new CcpEntityBulkHandlerTransferRecordToTwinEntity(this, nothingToTransfer);
		CcpSelectUnionAll unionAll = super.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, super.functionToDeleteKeysInTheCache, transfer);
		boolean existedBeforeTheDeletion = this.isPresentInThisUnionAll(unionAll, json);
		return existedBeforeTheDeletion;
	}

	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		ArrayList<CcpEntity> result = new ArrayList<CcpEntity>(associatedEntities);
		CcpEntity wrapedTwinEntity = this.getWrapedTwinEntity();
		List<CcpEntity> twinAssociatedEntities = wrapedTwinEntity.getAssociatedEntities();
		result.addAll(twinAssociatedEntities);
		ArrayList<CcpEntity> distinctAssociatedEntities = new ArrayList<>(new HashSet<>(result));
		return distinctAssociatedEntities;
	}

	/**
	 * Reads from the main entity; if the record was transferred to the twin, signals it with {@code REDIRECT}; if it is not
	 * in either of the two by the direct search, delegates inward along the chain.
	 *
	 * <p>The result of {@code getOneByIdAnyWhere} carries one key per searched entity, with an empty json
	 * when the record is not in it. That is why "found" means the entity's json is not empty — and not
	 * the key existing, which is always true. Testing the presence of the key made the method return an empty json
	 * as if it were the record (the case of disposable twins, whose id in the index embeds the period and does not
	 * show up in the direct search) and left {@code REDIRECT} unreachable.
	 *
	 * <p>When the direct search finds nothing, both ends are queried again through the decorated
	 * chain, which knows the real id of each index — in disposable twins, the id with the period. Only
	 * then is the record considered missing. The extra round trips to the database happen only on this path.
	 */
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {

		CcpJsonRepresentation oneByIdAnyWhere = super.getOneByIdAnyWhere(json);

		CcpJsonRepresentation recordInMainEntity = oneByIdAnyWhere.getInnerJson(this);
		boolean foundInMainEntity = false == recordInMainEntity.isEmpty();

		if(foundInMainEntity) {
			return recordInMainEntity;
		}

		CcpEntity twinEntity = this.getTwinEntity();
		CcpJsonRepresentation recordInTwinEntity = oneByIdAnyWhere.getInnerJson(twinEntity);
		boolean foundInTwinEntity = false == recordInTwinEntity.isEmpty();

		if(foundInTwinEntity) {
			throw this.getRedirect(json, twinEntity);
		}

		boolean foundInMainEntityByTheDecoratedChain = this.entity.exists(json);

		if(foundInMainEntityByTheDecoratedChain) {
			CcpJsonRepresentation oneById =  this.entity.getOneById(json);
			return oneById;
		}

		boolean foundInTwinEntityByTheDecoratedChain = twinEntity.exists(json);

		if(foundInTwinEntityByTheDecoratedChain) {
			throw this.getRedirect(json, twinEntity);
		}

		CcpJsonRepresentation oneById =  this.entity.getOneById(json);
		return oneById;
	}
	
	private CcpErrorFlowDisturb getRedirect(CcpJsonRepresentation json, CcpEntity twinEntity) {
		String id = twinEntity.calculateId(json);
		String errorMessage = String.format("The id '%s' has been moved from '%s' to '%s' ", id, this,  twinEntity);
		CcpErrorFlowDisturb redirectError = new CcpErrorFlowDisturb(json, CcpProcessStatusDefault.REDIRECT, errorMessage, new CcpJsonFieldName[0]);
		return redirectError;
	}

	public CcpEntity getTwinEntity(CcpEntityDecoratorType... decoratorsToAvoid) {
		boolean twinAlreadyResolved = this.twin != null;
	
		if(twinAlreadyResolved) {
			return this.twin;
		}
		
		String twinEntityName = this.clazz.getAnnotation(CcpEntityTwin.class).twinEntityName();
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		boolean isTwinEntityName = entityDetails.entityName.equals(twinEntityName);

		boolean isNotTwin = false == isTwinEntityName;
		
		if(isNotTwin) {
			this.twin = CcpEntityFactory.getEntity(this.clazz, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName(), decoratorsToAvoid);
			return this.twin;
		}
		CcpReflectionConstructorDecorator configuratorConstructor = new CcpReflectionConstructorDecorator(this.clazz);

		CcpEntityConfigurator configurator = configuratorConstructor.newInstance();
		this.twin = configurator.getEntity();
		return this.twin;
	}

	/**
	 * Writes to the main entity and deletes from the twin. The {@code unionAll} triggered before the bulk tells whether the
	 * main entity already had the record, which is what distinguishes an insert from an update.
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
		List<CcpJsonRepresentation> mainParametersToSearch = this.entity.getParametersToSearch(json);
		List<CcpJsonRepresentation> parametersToSearch =  new ArrayList<CcpJsonRepresentation>(mainParametersToSearch);
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
			CcpEntity innerWrapedEntity = wrapedEntity.getWrapedEntity();
			var innerWrapedEntityClass = innerWrapedEntity.getClass();
			boolean twinIsMissing = wrapedEntityClass.equals(innerWrapedEntityClass);
			if(twinIsMissing) {
				CcpErrorEntityIsNotTwin notTwinError = new CcpErrorEntityIsNotTwin(this.entity);
				throw notTwinError;
			}
		}
		CcpEntity twinDecoratorWrapedEntity = wrapedEntity.getWrapedEntity();

		return twinDecoratorWrapedEntity;
	}

	/**
	 * Exception thrown when looking for the twin entity of an entity that was not decorated as a twin,
	 * that is, the decorator chain was walked to the end without finding a {@code DecoratorTwinEntity}.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorEntityIsNotTwin extends RuntimeException {
		/**
		 * Builds the message stating which entity has no twin.
		 * @param entity the entity that is not a twin
		 */
		private CcpErrorEntityIsNotTwin(CcpEntity entity) {
			super(entity + " is not a twin entity");
		}
	}

}
