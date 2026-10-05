package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerSave;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

/**
 * Abstract specialization of {@code CcpEntityDelegator} that provides default implementations of
 * {@code save}, {@code delete}, {@code deleteAnyWhere}, {@code transferDataTo} and {@code copyDataTo}
 * using bulk operations. It is the base for every decorator that needs write operations with
 * bulk support and cache invalidation.
 */
public abstract class CcpDefaultEntityDelegator<CcpAnnotation> extends CcpEntityDelegator{
	
	/** Receives the cache keys to invalidate around the bulk operations. */
	protected final Consumer<String[]> functionToDeleteKeysInTheCache;
	/** Runs the union-all searches and the bulk operations. */
	protected final CcpExecuteBulkOperation executeBulkOperation; 

	
	/**
	 * Wraps the entity with the bulk machinery.
	 * @param entity the wrapped entity
	 * @param executeBulkOperation runs the union-all searches and the bulk operations
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 */
	public CcpDefaultEntityDelegator(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		super(entity);
		this.functionToDeleteKeysInTheCache = functionToDeleteKeysInTheCache;
		this.executeBulkOperation = executeBulkOperation;
	}
	

	/**
	 * Removes the record through bulk. Since the bulk does not return the outcome of each item, the previous existence
	 * of the record is checked before the removal so that the return value means the same as in
	 * {@code CcpEntity.delete}: the record existed and was removed.
	 */
	public boolean delete(CcpJsonRepresentation json) {
		boolean existedBeforeTheDeletion = this.exists(json);
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		this.executeBulkOperation.executeBulk(bulkItems, this.functionToDeleteKeysInTheCache);
		return existedBeforeTheDeletion;
	}

	/**
	 * Removes the record from the main entity and from the twin. Returns {@code true} if it existed in at
	 * least one of the two before the removal.
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean existedInTheMainEntity = this.exists(json);
		boolean existedInTheTwinEntity = false;

		List<CcpBulkItem> mainEntityDeleteItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);

		List<CcpBulkItem> bulkItems = new ArrayList<>(mainEntityDeleteItems);
		try {
			CcpEntity twinEntity = this.getTwinEntity();
			List<CcpBulkItem> bulkItemsTwin = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
			bulkItems.addAll(bulkItemsTwin);
			existedInTheTwinEntity = twinEntity.exists(json);
		} catch (UnsupportedOperationException e) {
		}
		Stream<CcpBulkItem> bulkItemsStream = bulkItems.stream();
		var deleteItemsStream = bulkItemsStream.map(item -> new CcpBulkItem(item, CcpBulkEntityOperationType.delete));

		List<CcpBulkItem> deleteItems = deleteItemsStream
		.collect(Collectors.toList());
		this.executeBulkOperation.executeBulk(deleteItems, this.functionToDeleteKeysInTheCache);

		boolean existedBeforeTheDeletion = existedInTheMainEntity || existedInTheTwinEntity;
		return existedBeforeTheDeletion;
	}

	/**
	 * Saves the record through bulk. The {@code unionAll} triggered before the bulk already tells whether the record
	 * existed, so it is what distinguishes an insert from an update.
	 *
	 * <p>Only the items of the entity itself get a handler. The items of auxiliary tables (the versionable's
	 * history row, the disposable's copy) do not need their own handler: the handler of the
	 * main entity calls {@code toBulkItems} of the complete entity, which goes through the same
	 * decorators again and regenerates them. One handler per auxiliary item saved each of them twice — in the
	 * versionable, two history rows per {@code save}.
	 */
	public boolean save(CcpJsonRepresentation json) {
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.create);
		CcpEntityMetaData thisEntityDetails = this.getEntityMetaData();
		List<CcpBulkHandlerSave> handlers = new ArrayList<>();
		CcpJsonRepresentation parametersToSearch = CcpOtherConstants.EMPTY_JSON;
		for (CcpBulkItem bulkItem : bulkItems) {
			CcpEntityMetaData itemEntityDetails = bulkItem.entity.getEntityMetaData();
			boolean isThisEntity = itemEntityDetails.entityName.equals(thisEntityDetails.entityName);
			boolean isAnAuxiliaryEntity = false == isThisEntity;
			if(isAnAuxiliaryEntity) {
				// the fields of an auxiliary record never overwrite the ones of this entity: the disposable record
				// keeps the whole original record in a field named "json", and an entity that also has a "json"
				// field (the bot session) was saved with its own record nested inside it
				parametersToSearch = bulkItem.json.mergeWithAnotherJson(parametersToSearch);
				continue;
			}
			parametersToSearch = parametersToSearch.mergeWithAnotherJson(bulkItem.json);
			CcpBulkHandlerSave handler = new CcpBulkHandlerSave(bulkItem.entity);
			handlers.add(handler);
		}
		int handlersCount = handlers.size();
		CcpBulkHandlerSave[] handlersArray = handlers.toArray(new CcpBulkHandlerSave[handlersCount]);
		parametersToSearch = json.redoJson(parametersToSearch);
		CcpSelectUnionAll unionAll = this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(parametersToSearch, this.functionToDeleteKeysInTheCache, handlersArray);
		boolean alreadyExisted = this.isPresentInThisUnionAll(unionAll, parametersToSearch);
		boolean inserted = false == alreadyExisted;
		return inserted;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String calculateId(CcpJsonRepresentation json) {
		String recordId = this.entity.calculateId(json);
		return recordId;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		return entityDetails;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	/**
	 * Looks for the record in every associated entity whose primary key the json is able to build.
	 *
	 * <p>The associated entities include auxiliary tables that internal decorators add — the versionable's
	 * history, the disposable's copy — and they have their own primary key ({@code entity},
	 * {@code id}...). When the json is the one of the business record, that key does not exist in it, and computing the
	 * id there blew up with {@code CcpErrorEntityPrimaryKeyIsMissing}: that is what made {@code getOneById}
	 * break in every twin entity that is also versionable or disposable. Whoever needs the auxiliary
	 * table in the result (the disposable) merges its key into the json before reaching this point.
	 */
	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		List<CcpEntity> allAssociatedEntities = getAssociatedEntities();
		Stream<CcpEntity> associatedEntitiesStream = allAssociatedEntities.stream();
		var withTheKeyInTheJson = associatedEntitiesStream.filter(entity -> json.containsAllFields(entity.getEntityMetaData().primaryKeyNames));
		List<CcpEntity> associatedEntities = withTheKeyInTheJson.collect(Collectors.toList());
		int associatedEntitiesSize = associatedEntities.size();

		CcpEntity[] entitiesToSearch = associatedEntities.toArray(new CcpEntity[associatedEntitiesSize]);
		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, entitiesToSearch);
		
		CcpJsonRepresentation result = CcpOtherConstants.EMPTY_JSON;
		
		for (CcpEntity entity : associatedEntities) {
			String mainId = entity.calculateId(json);
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			CcpJsonRepresentation record = unionAll.getEntityRow(entityDetails.entityName, mainId);
			result = result
					.put(entity, record);

		}
		
		return result;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpEntity getTwinEntity(CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntity twinEntity = this.entity.getTwinEntity(decoratorsToAvoid);
		return twinEntity;
	}

	/**
	 * Returns the wrapped entity, the next link of the decorator chain.
	 * @return the wrapped entity
	 */
	public CcpEntity getWrapedEntity() {
		return this.entity;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean exists(CcpJsonRepresentation json) {
		boolean exists = this.entity.exists(json);
		return exists;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public <T> T throwException() {
		T result = this.entity.throwException();
		return result;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		return associatedEntities;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}

	/**
	 * Moves the record to the given entity. Until 2026-09-27 this method received {@code CcpEntity...}:
	 * it was an overload, it did not override the interface method, and no transfer ever reached it. Rules
	 * in {@code CcpEntityDataMover}.
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean transfered = CcpEntityDataMover.transfer(this, entityToTransferData, json, this.executeBulkOperation, this.functionToDeleteKeysInTheCache);
		return transfered;
	}

	/** Copies the record to the given entity, without removing it from here. Rules in {@code CcpEntityDataMover}. */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToCopyData) {
		boolean copied = CcpEntityDataMover.copy(this, entityToCopyData, json, this.executeBulkOperation, this.functionToDeleteKeysInTheCache);
		return copied;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation validatedJson = this.entity.validateJson(json);
		return validatedJson;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation idToSearchDisposableRecord = this.entity.getIdToSearchDisposableRecord(json);
		return idToSearchDisposableRecord;
	}
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
		return recordFromUnionAll;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String getValue() {
		String value = this.entity.getValue();
		return value;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String name() {
		String name = this.entity.name();
		return name;
	}

}
