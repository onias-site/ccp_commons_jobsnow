package com.ccp.especifications.db.utils.decorators.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;
import com.ccp.especifications.db.utils.CcpEntityField;

public abstract class CcpEntityDelegator implements CcpEntity{

	protected final CcpEntity entity;

	public CcpEntityDelegator(CcpEntity entity) {
		this.entity = entity;
	}

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, json);
		return recordFromUnionAll;
	}

	public String getEntityName() {
		String entityName = this.entity.getEntityName();
		return entityName;
	}

	public String calculateId(CcpJsonRepresentation json) {
		String calculateId = this.entity.calculateId(json);
		return calculateId;
	}

	public CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		CcpJsonRepresentation primaryKeyValues = this.entity.getPrimaryKeyValues(json);
		return primaryKeyValues;
	}

	public CcpBulkItem getRecordCopyToBulkOperation(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {
		CcpBulkItem recordCopyToBulkOperation = this.entity.getRecordCopyToBulkOperation(json, operation);
		return recordCopyToBulkOperation;
	}

	public CcpEntityField[] getFields() {
		CcpEntityField[] fields = this.entity.getFields();
		return fields;
	}

	public CcpEntity getTwinEntity() {
		CcpEntity twinEntity = this.entity.getTwinEntity();
		return twinEntity;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> ifNotFound) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json, ifNotFound);
		return oneById;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneById(String id) {
		CcpJsonRepresentation oneById = this.entity.getOneById(id);
		return oneById;
	}

	public boolean exists(String id) {
		boolean exists = this.entity.exists(id);
		return exists;
	}

	public boolean exists(CcpJsonRepresentation json) {
		boolean exists = this.entity.exists(json);
		return exists;
	}

	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json) {
		CcpJsonRepresentation createOrUpdate = this.entity.createOrUpdate(json);
		return createOrUpdate;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.entity.delete(json);
		return delete;
	}

	public boolean delete(String id) {
		boolean delete = this.entity.delete(id);
		return delete;
	}

	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json, String id) {
		CcpJsonRepresentation createOrUpdate = this.entity.createOrUpdate(json, id);
		return createOrUpdate;
	}

	public CcpJsonRepresentation getOnlyExistingFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation onlyExistingFields = this.entity.getOnlyExistingFields(json);
		return onlyExistingFields;
	}

	public List<String> getPrimaryKeyNames() {
		List<String> primaryKeyNames = this.entity.getPrimaryKeyNames();
		return primaryKeyNames;
	}

	public CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
  		CcpJsonRepresentation requiredEntityRow = this.entity.getRequiredEntityRow(unionAll, json);
		return requiredEntityRow;
	}

	public boolean isPresentInThisJsonInMainEntity(CcpJsonRepresentation json) {
		boolean presentInThisJsonInMainEntity = this.entity.isPresentInThisJsonInMainEntity(json);
		return presentInThisJsonInMainEntity;
	}

	public CcpJsonRepresentation getInnerJsonFromMainAndTwinEntities(CcpJsonRepresentation json) {
		CcpJsonRepresentation innerJsonFromMainAndTwinEntities = this.entity.getInnerJsonFromMainAndTwinEntities(json);
		return innerJsonFromMainAndTwinEntities;
	}

	public CcpJsonRepresentation getData(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache) {
		CcpJsonRepresentation data = this.entity.getData(json, functionToDeleteKeysInTheCache);
		return data;
	}

	public String[] getEntitiesToSelect() {
		String[] entitiesToSelect = this.entity.getEntitiesToSelect();
		return entitiesToSelect;
	}

	public ArrayList<Object> getSortedPrimaryKeyValues(CcpJsonRepresentation json) {
		ArrayList<Object> sortedPrimaryKeyValues = this.entity.getSortedPrimaryKeyValues(json);
		return sortedPrimaryKeyValues;
	}

	public CcpEntity[] getThisEntityAndHisTwinEntity() {
		CcpEntity[] thisEntityAndHisTwinEntity = this.entity.getThisEntityAndHisTwinEntity();
		return thisEntityAndHisTwinEntity;
	}
	
	public final String toString() {
		String entityName = this.getEntityName();
		return entityName;
	}

	public CcpBulkItem toBulkItemToCreateOrDelete(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpBulkItem bulkItemToCreateOrDelete = this.entity.toBulkItemToCreateOrDelete(unionAll, json);
		return bulkItemToCreateOrDelete;
	}

	public CcpJsonRepresentation getOnlyExistingFieldsAndHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation onlyExistingFieldsAndHandledJson = this.entity.getOnlyExistingFieldsAndHandledJson(json);
		return onlyExistingFieldsAndHandledJson;
	}

	public Function<CcpJsonRepresentation, CcpJsonRepresentation> getOperationCallback(
			CcpEntityCrudOperationType operation) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> operationCallback = this.entity.getOperationCallback(operation);
		return operationCallback;
	}

	public CcpJsonRepresentation getEntityDetails() {
		CcpJsonRepresentation entityDetails = this.entity.getEntityDetails();
		return entityDetails;
	}

	public CcpBulkItem getMainBulkItem(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {
		CcpBulkItem mainBulkItem = this.entity.getMainBulkItem(json, operation);
		return mainBulkItem;
	}

	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}

	public CcpJsonRepresentation getTransformedJsonBeforeAnyCrudOperations(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonBeforeOperation = this.entity.getTransformedJsonBeforeAnyCrudOperations(json);
		return transformedJsonBeforeOperation;
	}

	public CcpJsonRepresentation getTransformedJsonAfterOperation(CcpJsonRepresentation json,
			CcpEntityCrudOperationType operation) {
		CcpJsonRepresentation transformedJsonAfterOperation = this.entity.getTransformedJsonAfterOperation(json, operation);
		return transformedJsonAfterOperation;
	}

	public CcpEntity validateJson(CcpJsonRepresentation json) {
		CcpEntity validateJson = this.entity.validateJson(json);
		return validateJson;
	}

	public CcpBulkHandlerTransferRecordToReverseEntity getTransferRecordToReverseEntity() {
		CcpBulkHandlerTransferRecordToReverseEntity transferRecordToReverseEntity = this.entity.getTransferRecordToReverseEntity();
		return transferRecordToReverseEntity;
	}

	
}
