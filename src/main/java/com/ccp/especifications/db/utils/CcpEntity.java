package com.ccp.especifications.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntitySpecifications;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;
import com.ccp.utils.CcpHashAlgorithm;

public interface CcpEntity{
	enum JsonFieldNames implements CcpJsonFieldName{
		entity, id, entityName, primaryKeyNames, _entities
	}

	default CcpBulkItem getMainBulkItem(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {
		CcpBulkItem bulkItem = this.toBulkItems(json, operation).stream().filter(x -> x.entity == this).findFirst().get();
		return bulkItem;
	}
	
	default List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		
		String id = this.calculateId(json);

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();
		
		String entityName = this.getEntityName();
		
		CcpJsonRepresentation mainRecord = CcpOtherConstants.EMPTY_JSON
		.getDynamicVersion().put(fieldNameToEntity, entityName)
		.getDynamicVersion().put(fieldNameToId, id)
		;
		List<CcpJsonRepresentation> asList = Arrays.asList(mainRecord);
		return asList;
	}

	default boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		String index = this.getEntityName();
		String id = this.calculateId(json);

		boolean present = unionAll.isPresent(index, id);
		
		return present;
	}
	

	String getEntityName();

	default String calculateId(CcpJsonRepresentation json) {
		List<String> primaryKeyNames = this.getPrimaryKeyNames();
		
		boolean hasNoPrimaryKey = primaryKeyNames.isEmpty();
		
		if(hasNoPrimaryKey) {
			String string = UUID.randomUUID().toString();
			String hash = new CcpStringDecorator(string).hash().asString(CcpHashAlgorithm.SHA1);
			return hash;
		}
		
		ArrayList<Object> sortedPrimaryKeyValues = this.getSortedPrimaryKeyValues(json);
		
		String replace = sortedPrimaryKeyValues.toString().replace("[", "").replace("]", "");
		CcpHashDecorator hash2 = new CcpStringDecorator(replace).hash();
		String hash = hash2.asString(CcpHashAlgorithm.SHA1);
		return hash;
	}
	
	default CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		
		List<String> onlyPrimaryKeyNames = this.getPrimaryKeyNames();
	
		boolean primaryKeyMissing = false == json.containsAllFields(onlyPrimaryKeyNames);
		
		if(primaryKeyMissing) {
			throw new CcpErrorEntityPrimaryKeyIsMissing(this, json);
		}
		CcpJsonRepresentation onlyPrimaryKeyValues = json.getJsonPiece(onlyPrimaryKeyNames);
		
		CcpJsonRepresentation transformedJson = this.getTransformedJsonByEachFieldInJson(onlyPrimaryKeyValues);
		
		CcpJsonRepresentation jsonPiece = transformedJson.getJsonPiece(onlyPrimaryKeyNames);
		return jsonPiece;
	}
	
	CcpEntityField[] getFields();
	
	default List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {
		String calculateId = this.calculateId(json);
		CcpBulkItem ccpBulkItem = new CcpBulkItem(json, operation, this, calculateId);
		return Arrays.asList(ccpBulkItem);
	}
	
	default CcpEntity getTwinEntity() {
		throw new UnsupportedOperationException();
	}

	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String calculateId = this.calculateId(json);
			String entityName = this.getEntityName();
			CcpJsonRepresentation oneById = crud.getOneById(entityName, calculateId);
			return oneById;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			CcpJsonRepresentation execute = ifNotFound.apply(json);
			return execute;
		}
	}

	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		String entityName = this.getEntityName();
		CcpJsonRepresentation md = this.getOneById(json, x -> {throw new CcpErrorFlowDisturb(x.put(JsonFieldNames.entity, entityName), CcpProcessStatusDefault.NOT_FOUND);});
		return md;
	}
	

	default CcpJsonRepresentation getOneById(String id) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String entityName = this.getEntityName();
			CcpJsonRepresentation md = crud.getOneById(entityName, id);
			return md;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			String entityName = this.getEntityName();
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.id, id).put(JsonFieldNames.entity, entityName);
			throw new CcpErrorFlowDisturb(put, CcpProcessStatusDefault.NOT_FOUND);
		}
	}
	
	default boolean exists(String id) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String entityName = this.getEntityName();
		boolean exists = crud.exists(entityName, id);
		return exists;
		
	}
	
	default boolean exists(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String id = this.calculateId(json);
		String entityName = this.getEntityName();
		boolean exists = crud.exists(entityName, id);
		return exists;
	}
	
	default CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation handledJson = this.getTransformedJsonByEachFieldInJson(json);
		this.validateJson(handledJson.putAll(json));
		CcpJsonRepresentation transformedJsonBeforeOperation = this.getTransformedJsonBeforeOperation(handledJson, CcpEntityOperationType.save);
		CcpJsonRepresentation onlyExistingFields = this.getOnlyExistingFields(transformedJsonBeforeOperation);
		String id = this.calculateId(json);
		this.save(onlyExistingFields, id);
		CcpJsonRepresentation transformedJsonAfterOperation = this.getTransformedJsonAfterOperation(transformedJsonBeforeOperation, CcpEntityOperationType.save);
		return transformedJsonAfterOperation;
	}
	
	default CcpJsonRepresentation save(CcpJsonRepresentation onlyExistingFields, String id) {
		String entityName = this.getEntityName();
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpJsonRepresentation save = crud.save(entityName, onlyExistingFields, id);
		return save;
	}

	default CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		String calculateId = this.calculateId(json);
		String entityName = this.getEntityName();
		crud.delete(entityName, calculateId);
		CcpJsonRepresentation transformedJsonAfterOperation = this.getTransformedJsonAfterOperation(json, CcpEntityOperationType.delete);
		return transformedJsonAfterOperation;
	}

	default CcpJsonRepresentation getOnlyExistingFields(CcpJsonRepresentation json) {
		CcpEntityField[] fields = this.getFields();
		CcpJsonRepresentation subMap = json.getJsonPiece(fields);
		return subMap;
	}
	
	default CcpJsonRepresentation getOnlyExistingFieldsAndHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJson = this.getTransformedJsonByEachFieldInJson(json);
		CcpJsonRepresentation onlyExistingFields = this.getOnlyExistingFields(transformedJson);
		return onlyExistingFields;
	}

	default List<String> getPrimaryKeyNames() {
		CcpEntityField[] fields = this.getFields();
		List<String> onlyPrimaryKey = new ArrayList<>(Arrays.asList(fields).stream().filter(x -> x.primaryKey).map(x -> x.name()).collect(Collectors.toList()));
		return onlyPrimaryKey;
	}
	
	default CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		boolean notFound = false == this.isPresentInThisUnionAll(unionAll, json);

		if(notFound) {
			throw new CcpErrorBulkEntityRecordNotFound(this, json);
		}
		
		CcpJsonRepresentation entityRow = this.getRecordFromUnionAll(unionAll, json);
		
		return entityRow;
	}
	default CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		String id = this.calculateId(json);
		String index = this.getEntityName();
		
		CcpJsonRepresentation jsonValue = unionAll.getEntityRow(index, id);
		
		return jsonValue;
	}

	default boolean isPresentInThisJsonInMainEntity(CcpJsonRepresentation json) {
		CcpJsonRepresentation innerJsonFromPath = json.getInnerJsonFromPath(JsonFieldNames._entities, this.getEntityName());
		boolean empty = innerJsonFromPath.isEmpty();
		return false == empty;
	}

	default CcpJsonRepresentation getInnerJsonFromMainAndTwinEntities(CcpJsonRepresentation json) {
		String entityName = this.getEntityName();
		CcpJsonRepresentation j1 = json.getInnerJsonFromPath(JsonFieldNames._entities, entityName);
		return j1;
	}
	
	default CcpJsonRepresentation getData(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache) {
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll searchResults = crud.unionBetweenMainAndTwinEntities(json, functionToDeleteKeysInTheCache, this);
		
		CcpJsonRepresentation requiredEntityRow = this.getRequiredEntityRow(searchResults, json);

		return requiredEntityRow;
	}
	
	default String[] getEntitiesToSelect() {
		String entityName = this.getEntityName();
		String[] resourcesNames = new String[]{entityName};
		return resourcesNames;
	}
	default ArrayList<Object> getSortedPrimaryKeyValues(CcpJsonRepresentation json) {

		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		TreeMap<String, Object> treeMap = new TreeMap<>(primaryKeyValues.content);
		Collection<Object> values2 = treeMap.values();
		ArrayList<Object> onlyPrimaryKeys = new ArrayList<>(values2);
		return onlyPrimaryKeys;
	}
	
	default CcpEntity[] getThisEntityAndHisTwinEntity() {
		return new CcpEntity[] {this};
	}

	
	default CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		return json -> operation.execute(this, json);
	}
	
	default CcpJsonRepresentation getEntityDetails() {
		List<String> primaryKeyNames = this.getPrimaryKeyNames();
		String entityName = this.getEntityName();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
		.put(JsonFieldNames.primaryKeyNames, primaryKeyNames)
		.put(JsonFieldNames.entityName, entityName);
		return put;
	}
	
	CcpJsonRepresentation getTransformedJsonByEachFieldInJson(CcpJsonRepresentation json);

	CcpJsonRepresentation getTransformedJsonBeforeOperation(CcpJsonRepresentation json, CcpEntityOperationType operation);

	CcpJsonRepresentation getTransformedJsonAfterOperation(CcpJsonRepresentation json, CcpEntityOperationType operation);
	
	CcpEntity validateJson(CcpJsonRepresentation json);
	
	CcpEntityBulkHandlerTransferRecordToReverseEntity getTransferRecordToReverseEntity();
	
	Class<?> getConfigurationClass();
	
	default Class<?> getJsonValidationClass(){
		Class<?> configurationClass = this.getConfigurationClass();
		CcpEntitySpecifications especifications = CcpEntityOperationType.getEspecifications(configurationClass);
		Class<?> classWithFieldsValidationsRules = especifications.entityValidation();
		return classWithFieldsValidationsRules;
	}
	
	default CcpJsonRepresentation transferToReverseEntity(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException();
	}
	
	default CcpEntity getWrapedEntity() {
		return this;
	}
}
