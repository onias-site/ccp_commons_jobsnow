package com.ccp.especifications.db.utils.entity;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Step of a procedure of {@code CcpGetEntityId} that deletes a given record from an entity (in a twin entity, the delete
 * moves it to the other side) and hands the json of the procedure on unchanged.
 */
public class CcpBusinessDeleteRecordFromEntity implements CcpBusiness {

	/** The entity the record is deleted from. */
	private final CcpEntity entity;

	/** The record (at least its key). */
	private final CcpJsonRepresentation record;

	/**
	 * Keeps what to delete.
	 * @param entity the entity the record is deleted from
	 * @param record the record (at least its key)
	 */
	public CcpBusinessDeleteRecordFromEntity(CcpEntity entity, CcpJsonRepresentation record) {
		this.entity = entity;
		this.record = record;
	}

	/**
	 * Deletes the record.
	 * @param json the json of the procedure
	 * @return the same json
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		this.entity.delete(this.record);
		return json;
	}
}
