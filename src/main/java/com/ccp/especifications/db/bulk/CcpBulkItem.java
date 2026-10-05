package com.ccp.especifications.db.bulk;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/** Fields of the summary of a bulk item. */
enum CcpBulkItemFields implements CcpJsonFieldName{
	/** The identifier of the record. */
	id,
	/** The name of the target entity. */
	entity,
	/** The operation applied to the record. */
	operation
}


/**
 * Represents a single item (record) within a bulk operation, gathering the record JSON,
 * the operation type ({@link CcpBulkEntityOperationType}), the target entity ({@link CcpEntity})
 * and the computed identifier. Used as the unit of work in the bulk operations pipeline.
 */
public class CcpBulkItem {
	/** Fields of the full JSON form of a bulk item. */
	enum JsonFieldNames implements CcpJsonFieldName{
		/** The record data. */
		json
	}

	/** The operation applied to the record. */
	public final CcpBulkEntityOperationType operation;
	/** The record data. */
	public final CcpJsonRepresentation json;
	/** The target entity. */
	public final CcpEntity entity;
	/** The identifier of the record in the entity. */
	public final String id;
	
	/**
	 * Creates a new item copying every field of {@code other}, but replacing the operation type.
	 * Useful for reprocessing (e.g. switching {@code create} to {@code update}).
	 *
	 * @param other original item to copy
	 * @param operation new operation type
	 */
	public CcpBulkItem(CcpBulkItem other, CcpBulkEntityOperationType operation) {
		this.operation = operation;
		this.entity = other.entity;
		this.json = other.json;
		this.id = other.id;
	}

	/**
	 * Creates an item with every field explicitly given.
	 *
	 * @param json record data
	 * @param operation bulk operation type
	 * @param entity target entity
	 * @param id record identifier
	 */
	public CcpBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity, String id) {
		this.operation = operation;
		this.entity = entity;
		this.json = json;
		this.id = id;
	}

	/**
	 * Returns a textual representation containing the item's entity, operation and id, omitting the full JSON.
	 *
	 * @return textual representation of the item
	 */
	public String toString() {
		CcpJsonRepresentation itemAsJson = this.asMap();
		CcpBulkItemFields[] summaryFields = CcpBulkItemFields.values();
		CcpJsonRepresentation jsonPiece = itemAsJson.getJsonPiece(summaryFields);
		String summary = jsonPiece.toString();
		return summary;
	}

	/**
	 * Serializes the item into a {@link CcpJsonRepresentation} containing the fields {@code operation},
	 * {@code entity} (name), {@code json} and {@code id}.
	 *
	 * @return JSON representing this item
	 */
	public CcpJsonRepresentation asMap() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		CcpJsonRepresentation jsonWithOperation = CcpOtherConstants.EMPTY_JSON
				.put(CcpBulkItemFields.operation, this.operation);
				CcpJsonRepresentation jsonWithEntity = jsonWithOperation
				.put(CcpBulkItemFields.entity, entityDetails.entityName);
				CcpJsonRepresentation jsonWithRecord = jsonWithEntity
				.put(JsonFieldNames.json, this.json);
				CcpJsonRepresentation jsonWithId = jsonWithRecord
				.put(CcpBulkItemFields.id, this.id);
		return jsonWithId;
	}
	
	/**
	 * Computes the hash from the combination {@code entity + "_" + id}, ensuring uniqueness per entity and identifier.
	 *
	 * @return item hash
	 */
	public int hashCode() {
		String entityWithSeparator = this.entity + "_";
		String hashSource = entityWithSeparator + this.id ;
		int hashCode = hashSource.hashCode();
		return hashCode;
	}

	/**
	 * Compares two {@link CcpBulkItem}s by entity and id; returns {@code false} if the object is of
	 * a different type or if the entity/id differ.
	 *
	 * @param obj object to compare
	 * @return {@code true} if entity and id are equal
	 */
	public boolean equals(Object obj) {
		try {
			CcpBulkItem other = (CcpBulkItem)obj;
			boolean entityEquals = other.entity.equals(this.entity);

			boolean differentEntity = false == entityEquals;
			
			if(differentEntity) {
				return false;
			}
			boolean idEquals = other.id.equals(this.id);

			boolean differentId = false == idEquals;
			
			if(differentId) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
