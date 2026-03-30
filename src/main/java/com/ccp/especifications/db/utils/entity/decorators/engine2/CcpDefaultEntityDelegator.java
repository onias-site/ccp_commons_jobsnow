package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity2;

public abstract class CcpDefaultEntityDelegator<CcpAnnotation> extends CcpEntityDelegator{
	
	protected final Consumer<String[]> functionToDeleteKeysInTheCache;
	protected final CcpExecuteBulkOperation executeBulkOperation; 
	
	public CcpDefaultEntityDelegator(CcpEntity2 entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		super(entity);
		this.functionToDeleteKeysInTheCache = functionToDeleteKeysInTheCache;
		this.executeBulkOperation = executeBulkOperation;
	}
	

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		this.executeBulkOperation.executeBulk(bulkItems);
		return json;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {

		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.delete);
		List<CcpBulkItem> collect = bulkItems.stream().map(item -> new CcpBulkItem(item, CcpBulkEntityOperationType.delete))
		.collect(Collectors.toList());
		this.executeBulkOperation.executeBulk(collect);

		return json;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		List<CcpBulkItem> bulkItems = this.toBulkItems(json, CcpBulkEntityOperationType.create);
		this.executeBulkOperation.executeBulk(bulkItems);
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

	public String[] getEntitiesToSelect() {
		String[] entitiesToSelect = this.entity.getEntitiesToSelect();
		return entitiesToSelect;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		List<CcpEntity2> associatedEntities = getAssociatedEntities();


		//FIXME
//		CcpEntity2[] array = associatedEntities.toArray(new CcpEntity2[associatedEntities.size()]);
//		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, array);
		CcpSelectUnionAll unionAll = crud.unionAll(json, this.functionToDeleteKeysInTheCache, (CcpEntity[])null);
		
		CcpJsonRepresentation result = CcpOtherConstants.EMPTY_JSON;
		
		for (CcpEntity2 entity : associatedEntities) {
			String mainId = entity.calculateId(json);
			CcpEntityDetails entityDetails = entity.getEntityDetails();
			CcpJsonRepresentation record = unionAll.getEntityRow(entityDetails.entityName, mainId);
			result = result
					.getDynamicVersion()
					.put(entityDetails.entityName, record);

		}
		
		return result;
	}

	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		CcpJsonRepresentation oneByIdOrHandleItIfThisIdWasNotFound = this.entity.getOneByIdOrHandleItIfThisIdWasNotFound(json, ifNotFound);
		return oneByIdOrHandleItIfThisIdWasNotFound;
	}

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, json);
		return recordFromUnionAll;
	}

	public CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation requiredEntityRow = this.entity.getRequiredEntityRow(unionAll, json);
		return requiredEntityRow;
	}

	public CcpEntity2 getTwinEntity() {
		CcpEntity2 twinEntity = this.entity.getTwinEntity();
		return twinEntity;
	}

	public CcpEntity2 getWrapedEntity() {
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

	public List<CcpEntity2> getAssociatedEntities() {
		List<CcpEntity2> associatedEntities = this.entity.getAssociatedEntities();
		return associatedEntities;
	}
	
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}


}
