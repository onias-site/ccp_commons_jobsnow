package com.ccp.especifications.db.utils.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.hash.CcpHashAlgorithm;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Central contract of the jobsnow entity system. Represents an index/table in Elasticsearch and defines
 * every CRUD operation, plus utilities for ID computation, union-all search, data transfer
 * and validation. Every entity enum of the system implements this interface.
 */
public interface CcpEntity  extends CcpJsonFieldName{
	/** Fields added to JSONs by the entity operations. */
	public static enum JsonFieldNames implements CcpJsonFieldName{
		/** The entity involved (e.g. in the context of a not-found error). */
		entity,
		/** Records loaded by a search, grouped by entity name. */
		_entities,
 
	}

	/**
	 * Returns the entity name (the index name in Elasticsearch) from the metadata.
	 */
	default String name() {
		CcpEntityMetaData entityMetaData = this.getEntityMetaData();
		return entityMetaData.entityName; 
	}

	/**
	 * Computes the id of the record: the SHA-1 (see {@code CcpHashDecorator.asString}) of the primary key values, sorted
	 * by field name and joined as {@code "v1, v2"}.
	 * @param json the record, holding the primary key values
	 * @return the record id
	 * @throws CcpEntityNoDefinedPrimaryKey when the entity declares no primary key
	 */
	default String calculateId(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		boolean hasNoPrimaryKey = entityDetails.primaryKeyNames.isEmpty();

		if(hasNoPrimaryKey) {
			CcpEntityNoDefinedPrimaryKey noPrimaryKeyError = new CcpEntityNoDefinedPrimaryKey(this);
			throw noPrimaryKeyError;
		}

		ArrayList<Object> sortedPrimaryKeyValues = entityDetails.getSortedPrimaryKeyValues(json);
		String primaryKeyValuesAsText = sortedPrimaryKeyValues.toString();
		String withoutOpeningBracket = primaryKeyValuesAsText.replace("[", "");

		String hashInput = withoutOpeningBracket.replace("]", "");
		CcpStringDecorator hashInputDecorator = new CcpStringDecorator(hashInput);
		CcpHashDecorator hashDecorator = hashInputDecorator.hash();
		String hash = hashDecorator.asString(CcpHashAlgorithm.SHA1);
		return hash;
	}

	/**
	 * Copies data from this entity to another entity without removing the original record.
	 * By default the operation is not supported and returns {@code false}.
	 */
	default boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return false;
	}

	/**
	 * Returns the entity metadata (fields, primary key, index name, etc.).
	 * Mandatory method to be implemented by every entity.
	 */
	CcpEntityMetaData getEntityMetaData();

	/**
	 * Removes the document matching the given JSON from this entity's index.
	 * The return value comes from the analysis {@code CcpCrud} makes of the removal response, considering the json
	 * returned by the database and the HTTP status: {@code true} when the record existed and was removed
	 * (200, with {@code result} equal to {@code deleted}) and {@code false} when it was not found
	 * (404, with {@code result} equal to {@code not_found}).
	 */
	default boolean delete(CcpJsonRepresentation json) {

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String recordId = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		boolean deleted = crud.delete(entityDetails.entityName, recordId);
		return deleted;
	}
	/**
	 * Delete variant without additional restrictions; delegates to {@code delete} by default.
	 */
	default boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean deleted = this.delete(json);
		return deleted;
	}

	/**
	 * Checks whether there is a document with the ID computed from the given JSON.
	 */
	default boolean exists(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String id = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		boolean exists = crud.exists(entityDetails.entityName, id);
		return exists;
	}

	/**
	 * Returns the list of associated entities. By default, a list containing only the entity itself;
	 * overridden in entities with a twin.
	 */
	default List<CcpEntity> getAssociatedEntities(){
		List<CcpEntity> associatedEntities = Arrays.asList(this);
		return associatedEntities;
	}

	/**
	 * Returns the JSON, possibly transformed, before operations. By default returns the same JSON unchanged.
	 */
	default CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		return json;
	}

	/**
	 * Finds a document by the computed ID; throws {@code CcpErrorFlowDisturb} with status {@code NOT_FOUND}
	 * if it is not found.
	 */
	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpBusiness whenNotFound = notFoundContext -> {
			CcpJsonRepresentation notFoundContextWithEntity = notFoundContext.put(JsonFieldNames.entity, this);
			CcpErrorFlowDisturb notFoundError = new CcpErrorFlowDisturb(notFoundContextWithEntity, CcpProcessStatusDefault.NOT_FOUND);
			throw notFoundError;
		};
		CcpJsonRepresentation recordFound = entityDetails.getOneByIdOrHandleItIfThisIdWasNotFound(json, whenNotFound);
		return recordFound;
	}

	/**
	 * Finds the document and returns it wrapped under the key of the entity itself.
	 */
	default CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.getOneById(json);
		CcpJsonRepresentation recordUnderEntity = CcpOtherConstants.EMPTY_JSON.put(this, oneById);
		return recordUnderEntity;
	}

	/**
	 * Builds the list of parameters (entity + id) needed for a union-all search.
	 */
	default List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {

		String id = this.calculateId(json);

		CcpDbRequester dbRequester = CcpDependencyInjection.getDependency(CcpDbRequester.class);

		String fieldNameToEntity = dbRequester.getFieldNameToEntity();
		String fieldNameToId = dbRequester.getFieldNameToId();

		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpFieldName entityKey = new CcpFieldName(fieldNameToEntity);
		CcpJsonRepresentation searchParameterWithEntity = CcpOtherConstants.EMPTY_JSON
		.put(entityKey, entityDetails.entityName);
		CcpFieldName idKey = new CcpFieldName(fieldNameToId);

		CcpJsonRepresentation mainRecord = searchParameterWithEntity
		.put(idKey, id)
		;
		List<CcpJsonRepresentation> parametersToSearch = Arrays.asList(mainRecord);
		return parametersToSearch;
	}

	/**
	 * Extracts this entity's record from a union-all result that has already been executed.
	 */
	default CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {

		CcpJsonRepresentation json = jsonSupplier.get();

		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		CcpJsonRepresentation handledJson = entityDetails.entity.getHandledJson(json);

		String id = this.calculateId(handledJson);

		CcpJsonRepresentation jsonValue = unionAll.getEntityRow(entityDetails.entityName, id);

		return jsonValue;
	}

	/**
	 * Returns the matching twin entity. By default throws {@code UnsupportedOperationException}.
	 */
	default CcpEntity getTwinEntity(CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntity twinEntity = this.throwException();
		return twinEntity;
	}

	/**
	 * Throws {@code UnsupportedOperationException} stating that the operation is not supported by this entity.
	 * Used as a safeguard for write operations on read-only entities.
	 */
	default <T>T throwException() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		String messageWithEntity = "The entity '" + entityDetails.entityName;
		String errorMessage = messageWithEntity + "' is just to read only";
		UnsupportedOperationException readOnlyError = new UnsupportedOperationException(errorMessage);
		throw readOnlyError;
	}

	/**
	 * Returns the "wrapped" entity (the base entity inside a decorator). By default returns {@code this}.
	 */
	default CcpEntity getWrapedEntity() {
		return this;
	}

	/**
	 * Checks whether this entity's document is present in a union-all result.
	 */
	default boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		String id = this.calculateId(json);

		boolean present = unionAll.isPresent(entityDetails.entityName, id);

		return present;
	}

	/**
	 * Saves the document in this entity's index, keeping only the fields that exist in the metadata.
	 * The return value comes from the analysis {@code CcpCrud} makes of the save response itself, considering
	 * both the json returned by the database and the HTTP status that comes with it: {@code true} when the
	 * document was inserted (201) and {@code false} when it already existed and was only updated (200).
	 */
	default boolean save(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String id = this.calculateId(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpJsonRepresentation response = crud.save(entityDetails.entityName, onlyExistingFields, id);
		boolean inserted = crud.isInsertedDocument(response);
		return inserted;
	}

	/**
	 * Converts the JSON into bulk operation items to be used by the bulk executor.
	 */
	default List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String recordId = this.calculateId(onlyExistingFields);
		CcpBulkItem bulkItem = new CcpBulkItem(onlyExistingFields, operation, entityDetails.entity, recordId);
		return Arrays.asList(bulkItem);
	}

	/**
	 * Transfers (moves) data from this entity to another entity, removing the original record.
	 * By default the operation is not supported and returns {@code false}.
	 */
	default boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return false;
	}

	/**
	 * Validates the JSON before write operations. By default returns the JSON without validation.
	 */
	default CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		return json;
	}

	/**
	 * Returns the ID used to search a disposable record.
	 * By default throws {@code UnsupportedOperationException}.
	 */
	default CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation idToSearch = this.throwException();
		return idToSearch;
	}

	/** Raised when the id of a record is computed for an entity that declares no primary key. */
	@SuppressWarnings("serial")
	public static class CcpEntityNoDefinedPrimaryKey extends RuntimeException {
		/** The entity without primary key. */
		public final CcpEntity entity;
		/**
		 * Builds the error naming the entity.
		 * @param entity the entity without primary key
		 */
		private CcpEntityNoDefinedPrimaryKey(CcpEntity entity) {
			super("The entity '" + entity.getEntityMetaData().entityName + "' has no defined primary key in his mapping");
			this.entity = entity;
		}
	}
}
