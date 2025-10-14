package com.ccp.especifications.db.utils.decorators.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
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

	public CcpEntityField[] getFields() {
		CcpEntityField[] fields = this.entity.getFields();
		return fields;
	}

	public CcpEntity getTwinEntity() {
		CcpEntity twinEntity = this.entity.getTwinEntity();
		return twinEntity;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json,
			CcpBusiness ifNotFound) {
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

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.entity.delete(json);
		return delete;
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

	public CcpJsonRepresentation getOnlyExistingFieldsAndHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation onlyExistingFieldsAndHandledJson = this.entity.getOnlyExistingFieldsAndHandledJson(json);
		return onlyExistingFieldsAndHandledJson;
	}

	public CcpBusiness getOperationCallback(CcpEntityOperationType operation) {
		CcpBusiness operationCallback = this.entity.getOperationCallback(operation);
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

	public CcpJsonRepresentation getTransformedJsonByEachFieldInJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonBeforeOperation = this.entity.getTransformedJsonByEachFieldInJson(json);
		return transformedJsonBeforeOperation;
	}

	public CcpJsonRepresentation getTransformedJsonAfterOperation(CcpJsonRepresentation json,
			CcpEntityOperationType operation) {
		CcpJsonRepresentation transformedJsonAfterOperation = this.entity.getTransformedJsonAfterOperation(json, operation);
		return transformedJsonAfterOperation;
	}

	public CcpEntity validateJson(CcpJsonRepresentation json) {
		CcpEntity validateJson = this.entity.validateJson(json);
		return validateJson;
	}

	public CcpEntityBulkHandlerTransferRecordToReverseEntity getTransferRecordToReverseEntity() {
		CcpEntityBulkHandlerTransferRecordToReverseEntity transferRecordToReverseEntity = this.entity.getTransferRecordToReverseEntity();
		return transferRecordToReverseEntity;
	}

	public Class<?> getConfigurationClass() {
		Class<?> configurationClass = this.entity.getConfigurationClass();
		return configurationClass;
	}

	public Class<?> getJsonValidationClass() {
		Class<?> jsonValidationClass = this.entity.getJsonValidationClass();
		return jsonValidationClass;
	}

	public CcpJsonRepresentation getTransformedJsonBeforeOperation(CcpJsonRepresentation json, CcpEntityOperationType operation) {
		CcpJsonRepresentation transformedJsonBeforeOperation = this.entity.getTransformedJsonBeforeOperation(json, operation);
		return transformedJsonBeforeOperation;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation createOrUpdate = this.entity.save(json);
		return createOrUpdate;
	}

	public CcpJsonRepresentation transferToReverseEntity(CcpJsonRepresentation json) {
		CcpJsonRepresentation transferToReverseEntity = this.entity.transferToReverseEntity(json);
		return transferToReverseEntity;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json, String id) {
		CcpJsonRepresentation save = this.entity.save(json, id);
		return save;
	}
	
}
