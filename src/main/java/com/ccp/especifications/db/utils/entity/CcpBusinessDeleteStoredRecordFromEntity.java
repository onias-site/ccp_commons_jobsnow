package com.ccp.especifications.db.utils.entity;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Step of a procedure of {@code CcpGetEntityId} that deletes, from an entity, the record the procedure read from it
 * (found under {@code _entities}), as it is stored; in a twin entity, the delete moves it to the other side. Hands the
 * json of the procedure on unchanged.
 */
public class CcpBusinessDeleteStoredRecordFromEntity implements CcpBusiness {

	/** The entity the record was read from and is deleted from. */
	private final CcpEntity entity;

	/**
	 * Keeps the entity.
	 * @param entity the entity the record was read from and is deleted from
	 */
	public CcpBusinessDeleteStoredRecordFromEntity(CcpEntity entity) {
		this.entity = entity;
	}

	/**
	 * Deletes the stored record.
	 * @param jsonWithTheStoredRecord the json of the procedure, holding the record under {@code _entities}
	 * @return the same json
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation jsonWithTheStoredRecord) {
		CcpJsonRepresentation storedRecord = jsonWithTheStoredRecord.getInnerJsonFromPath(CcpEntity.JsonFieldNames._entities, this.entity);
		this.entity.delete(storedRecord);
		return jsonWithTheStoredRecord;
	}
}
