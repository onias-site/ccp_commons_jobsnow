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
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerCreate;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerDelete;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerRead;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerSave;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Especialização abstrata de {@code CcpEntityDelegator} que fornece implementações padrão de
 * {@code save}, {@code delete}, {@code deleteAnyWhere}, {@code transferDataTo} e {@code copyDataTo}
 * usando operações bulk. É a base para todos os decorators que precisam de operações de escrita com
 * suporte a bulk e invalidação de cache.
 */
public abstract class CcpDefaultEntityDelegator<CcpAnnotation> extends CcpEntityDelegator{
	
	protected final Consumer<String[]> functionToDeleteKeysInTheCache;
	protected final CcpExecuteBulkOperation executeBulkOperation; 

	
	public CcpDefaultEntityDelegator(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		super(entity);
		this.functionToDeleteKeysInTheCache = functionToDeleteKeysInTheCache;
		this.executeBulkOperation = executeBulkOperation;
	}
	

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		this.executeBulkOperation.executeBulk(bulkItems, this.functionToDeleteKeysInTheCache);
		return json;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		List<CcpBulkItem> toBulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);

		List<CcpBulkItem> bulkItems = new ArrayList<>(toBulkItems);
		try {
			CcpEntity twinEntity = this.getTwinEntity();
			List<CcpBulkItem> bulkItemsTwin = twinEntity.toBulkItems(json, CcpBulkEntityOperationType.delete);
			bulkItems.addAll(bulkItemsTwin);
		} catch (UnsupportedOperationException e) { 
		} 
		Stream<CcpBulkItem> stream = bulkItems.stream();
		var streamMap = stream.map(item -> new CcpBulkItem(item, CcpBulkEntityOperationType.delete));

		List<CcpBulkItem> collect = streamMap
		.collect(Collectors.toList());
		this.executeBulkOperation.executeBulk(collect, this.functionToDeleteKeysInTheCache);
 
		return json;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.create);
		int size = bulkItems.size();
		CcpBulkHandlerSave[] array = new CcpBulkHandlerSave[size];
		int k = 0;
		CcpJsonRepresentation parametersToSearch = CcpOtherConstants.EMPTY_JSON;
		for (CcpBulkItem bulkItem : bulkItems) {
			CcpBulkHandlerSave handler = new CcpBulkHandlerSave(bulkItem.entity);
			array[k++] = handler;
			parametersToSearch = parametersToSearch.mergeWithAnotherJson(bulkItem.json);
		}
		parametersToSearch = json.redoJson(parametersToSearch);
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(parametersToSearch, this.functionToDeleteKeysInTheCache, array);
		return json;
	}
	
	public String calculateId(CcpJsonRepresentation json) {
		String calculateId = this.entity.calculateId(json);
		return calculateId;
	}

	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		return entityDetails;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		List<CcpEntity> associatedEntities = getAssociatedEntities();
		int associatedEntitiesSize = associatedEntities.size();


		CcpEntity[] array = associatedEntities.toArray(new CcpEntity[associatedEntitiesSize]);
//		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, array);
		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, array);
		
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

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public CcpEntity getTwinEntity(CcpEntityDecoratorTypes... decoratorsToAvoid) {
		CcpEntity twinEntity = this.entity.getTwinEntity(decoratorsToAvoid);
		return twinEntity;
	}

	public CcpEntity getWrapedEntity() {
		return this.entity;
	}

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	public boolean exists(CcpJsonRepresentation json) {
		boolean exists = this.entity.exists(json);
		return exists;
	}
	
	public <T> T throwException() {
		T throwException = this.entity.throwException();
		return throwException;
	}

	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		return associatedEntities;
	}
	
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity... entities) {
		List<CcpBulkItem> toBulkItems2 = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		Stream<CcpBulkItem> stream2 = toBulkItems2.stream();
		var stream2Map = stream2
		.map(x -> {
			CcpBulkHandlerDelete ccpBulkHandlerDelete = new CcpBulkHandlerDelete(x.entity, CcpOtherConstants.whenRecordWasNotFoundInTheEntityToSearch);
			return ccpBulkHandlerDelete;
			});


			List<CcpBulkHandlerDelete> delete = stream2Map
		.collect(Collectors.toList());
		
		List<CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>> all = new ArrayList<>(delete);
		
		for (CcpEntity entity : entities) {
			List<CcpBulkItem> toBulkItems3 = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			Stream<CcpBulkItem> stream3 = toBulkItems3.stream();
			var stream3Map = stream3
					.map(x -> new CcpBulkHandlerCreate(x.entity));
					List<CcpBulkHandlerCreate> create = stream3Map
					.collect(Collectors.toList());
		
			all.addAll(create);
		}
		int allSize = all.size();
		CcpHandleWithSearchResultsInTheEntity[] array = all.toArray(new CcpHandleWithSearchResultsInTheEntity[allSize]);
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, this.functionToDeleteKeysInTheCache, array);
	
		return json;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity... entities) {
		List<CcpBulkItem> toBulkItems4 = this.toBulkItems(json, CcpBulkEntityOperationType.noop);
		Stream<CcpBulkItem> stream4 = toBulkItems4.stream();
		var stream4Map = stream4
		.map(x -> {
			CcpBulkHandlerRead ccpBulkHandlerRead = new CcpBulkHandlerRead(x.entity, CcpOtherConstants.whenRecordWasNotFoundInTheEntityToSearch);
			return ccpBulkHandlerRead;
			});
			List<CcpBulkHandlerRead> read = stream4Map
		.collect(Collectors.toList());

		
		List<CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>> all = new ArrayList<>(read);
		
		for (CcpEntity entity : entities) {
			List<CcpBulkItem> toBulkItems5 = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			Stream<CcpBulkItem> stream5 = toBulkItems5.stream();
			var stream5Map = stream5
					.map(x -> new CcpBulkHandlerCreate(x.entity));
					List<CcpBulkHandlerCreate> create = stream5Map
					.collect(Collectors.toList());
		
			all.addAll(create);
		}
		int allSize2 = all.size();
		CcpHandleWithSearchResultsInTheEntity[] array = all.toArray(new CcpHandleWithSearchResultsInTheEntity[allSize2]);
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, this.functionToDeleteKeysInTheCache, array);
	
		return json;
	}
	
	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation validateJson = this.entity.validateJson(json);
		return validateJson;
	}
	
	public CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation idToSearchDisposableRecord = this.entity.getIdToSearchDisposableRecord(json);
		return idToSearchDisposableRecord;
	}
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
		return recordFromUnionAll;
	}

}
