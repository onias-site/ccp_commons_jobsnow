package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
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
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

public abstract class CcpDefaultEntityDelegator<CcpAnnotation> extends CcpEntityDelegator{
	
	protected final Consumer<String[]> functionToDeleteKeysInTheCache;
	protected final CcpExecuteBulkOperation executeBulkOperation; 
	Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch = jsn -> {
		CcpEntityDetails entityDetails = jsn.entity.getEntityDetails();
		throw new CcpErrorFlowDisturb(CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entityDetails.entityName), CcpProcessStatusDefault.NOT_FOUND);
	};

	
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

		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		List<CcpBulkItem> collect = bulkItems.stream().map(item -> new CcpBulkItem(item, CcpBulkEntityOperationType.delete))
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
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(parametersToSearch, this.functionToDeleteKeysInTheCache, array);
		return json;
	}
	
	public String calculateId(CcpJsonRepresentation json) {
		String calculateId = this.entity.calculateId(json);
		return calculateId;
	}

	public CcpEntityDetails getEntityDetails() {
		CcpEntityDetails entityDetails = this.entity.getEntityDetails();
		return entityDetails;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		List<CcpEntity> associatedEntities = getAssociatedEntities();


		CcpEntity[] array = associatedEntities.toArray(new CcpEntity[associatedEntities.size()]);
//		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, array);
		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, array);
		
		CcpJsonRepresentation result = CcpOtherConstants.EMPTY_JSON;
		
		for (CcpEntity entity : associatedEntities) {
			String mainId = entity.calculateId(json);
			CcpEntityDetails entityDetails = entity.getEntityDetails();
			CcpJsonRepresentation record = unionAll.getEntityRow(entityDetails.entityName, mainId);
			result = result
					.getDynamicVersion()
					.put(entityDetails.entityName, record);

		}
		
		return result;
	}

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, json);
		return recordFromUnionAll;
	}

	public CcpEntity getTwinEntity() {
		CcpEntity twinEntity = this.entity.getTwinEntity();
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


		List<CcpBulkHandlerDelete> delete = this.toBulkItems(json, CcpBulkEntityOperationType.delete).stream()
		.map(x -> {
			return new CcpBulkHandlerDelete(x.entity, this.whenRecordWasNotFoundInTheEntitySearch);
		})
		.collect(Collectors.toList());
		
		List<CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>> all = new ArrayList<>(delete);
		
		for (CcpEntity entity : entities) {
			List<CcpBulkHandlerCreate> create = entity.toBulkItems(json, CcpBulkEntityOperationType.create).stream()
					.map(x -> new CcpBulkHandlerCreate(x.entity))
					.collect(Collectors.toList());
		
			all.addAll(create);
		}
		CcpHandleWithSearchResultsInTheEntity[] array = all.toArray(new CcpHandleWithSearchResultsInTheEntity[all.size()]);
		this.executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(json, this.functionToDeleteKeysInTheCache, array);
	
		return json;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity... entities) {
		List<CcpBulkHandlerRead> read = this.toBulkItems(json, CcpBulkEntityOperationType.noop).stream()
		.map(x -> {
			return new CcpBulkHandlerRead(x.entity, this.whenRecordWasNotFoundInTheEntitySearch);
		})
		.collect(Collectors.toList());

		
		List<CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>> all = new ArrayList<>(read);
		
		for (CcpEntity entity : entities) {
			List<CcpBulkHandlerCreate> create = entity.toBulkItems(json, CcpBulkEntityOperationType.create).stream()
					.map(x -> new CcpBulkHandlerCreate(x.entity))
					.collect(Collectors.toList());
		
			all.addAll(create);
		}
		CcpHandleWithSearchResultsInTheEntity[] array = all.toArray(new CcpHandleWithSearchResultsInTheEntity[all.size()]);
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

}
