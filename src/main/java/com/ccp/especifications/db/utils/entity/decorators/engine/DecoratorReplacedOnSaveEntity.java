package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.List;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkScriptedUpdateFields;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator of the entities marked with {@code @CcpEntityReplacedOnSave}: {@code save} replaces the stored document
 * ({@code CcpCrud.replace}) instead of merging the fields sent into it ({@code CcpCrud.save}), so a field left out of
 * the json disappears from the record. Innermost of the chain (priority 0): every other decorator runs as before and
 * only the final write changes. The writes through the bulk (the twin entity saves this way) get the same treatment: an
 * {@code update} item becomes a {@code scriptedUpdate} that clears the stored document before putting the record. Created on 2026-11-10 (defect R09-1: a resume updated without {@code clt} or
 * {@code lastJob} kept the old values).
 */
class DecoratorReplacedOnSaveEntity extends CcpEntityDelegator {

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class (unused)
	 */
	public DecoratorReplacedOnSaveEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}

	/**
	 * Saves the document replacing the stored one, keeping only the fields that exist in the metadata.
	 * @param json the whole record
	 * @return {@code true} when the document was inserted, {@code false} when it replaced an existing one
	 */
	public boolean save(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String id = this.calculateId(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpJsonRepresentation response = crud.replace(entityDetails.entityName, onlyExistingFields, id);
		boolean inserted = crud.isInsertedDocument(response);
		return inserted;
	}

	/**
	 * The record saved replaces the stored one: the new record alone, nothing of the stored one.
	 * @param recordFound the stored record (ignored)
	 * @param newRecord the record being saved
	 * @return the new record
	 */
	public CcpJsonRepresentation getRecordToSave(CcpJsonRepresentation recordFound, CcpJsonRepresentation newRecord) {
		return newRecord;
	}

	/** Painless of the bulk replacement: the stored document keeps only the fields sent. */
	private static final String REPLACE_SCRIPT = "ctx._source.clear(); ctx._source.putAll(params);";

	/**
	 * Turns each {@code update} item of this entity into a {@code scriptedUpdate} that replaces the stored document
	 * (the plain {@code update} of the bulk is a partial {@code doc}, which merges). The other items go as they are.
	 * @param json the record
	 * @param operation the bulk operation
	 * @return the bulk items
	 */
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		boolean notAnUpdate = CcpBulkEntityOperationType.update != operation;
		if(notAnUpdate) {
			return bulkItems;
		}
		List<CcpBulkItem> replacingItems = new ArrayList<>();
		for (CcpBulkItem bulkItem : bulkItems) {
			CcpBulkItem replacingItem = this.toReplacingItem(bulkItem);
			replacingItems.add(replacingItem);
		}
		return replacingItems;
	}

	/**
	 * Builds the scripted update that replaces the document of an {@code update} item; items of other operations come back
	 * as they are.
	 * @param bulkItem the item
	 * @return the replacing item
	 */
	private CcpBulkItem toReplacingItem(CcpBulkItem bulkItem) {
		boolean notAnUpdate = CcpBulkEntityOperationType.update != bulkItem.operation;
		if(notAnUpdate) {
			return bulkItem;
		}
		CcpJsonRepresentation itemWithScript = CcpOtherConstants.EMPTY_JSON.put(CcpBulkScriptedUpdateFields.script, REPLACE_SCRIPT);
		CcpJsonRepresentation itemWithParams = itemWithScript.put(CcpBulkScriptedUpdateFields.params, bulkItem.json);
		CcpJsonRepresentation itemWithUpsert = itemWithParams.put(CcpBulkScriptedUpdateFields.upsert, bulkItem.json);
		CcpJsonRepresentation item = itemWithUpsert.put(CcpBulkScriptedUpdateFields.scriptedUpsert, false);
		CcpBulkItem replacingItem = new CcpBulkItem(item, CcpBulkEntityOperationType.scriptedUpdate, bulkItem.entity, bulkItem.id);
		return replacingItem;
	}
}
