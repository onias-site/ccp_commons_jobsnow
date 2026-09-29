package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Minimal base implementation of {@code CcpEntity} that wraps the metadata of an entity and
 * delegates every operation to the CRUD/bulk system through {@code CcpEntityMetaData}. It is the innermost
 * layer of the decorator chain built by {@code CcpEntityFactory}.
 */
class DefaultImplementationEntity implements CcpEntity{
	final CcpEntityMetaData entityDetails;

	/** Stores the metadata of the entity to be represented. */
	public DefaultImplementationEntity(CcpEntityMetaData entityDetails) {
		this.entityDetails = entityDetails;
	}

	public String toString() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		return entityDetails.entityName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity other) {
			CcpEntityMetaData entityDetails = this.getEntityMetaData();
			CcpEntityMetaData otherEntityDetails = other.getEntityMetaData();
			boolean sameEntityName = entityDetails.entityName.equals(otherEntityDetails.entityName);
			return sameEntityName;
		}
		return false;
	}

	public int hashCode() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		int hashCode = entityDetails.entityName.hashCode();
		return hashCode;
	}

	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData associatedMetaData = this.entityDetails.associateEntity();
		return associatedMetaData;
	}

	/**
	 * Transfer in the innermost layer: it is what serves the entities without a decorator that extends the
	 * default delegator. The cache of whoever is outside has already been cleared by {@code DecoratorCacheEntity}.
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean transfered = CcpEntityDataMover.transfer(this, entityToTransferData, json, CcpEntityDataMover.DIRECT_BULK, CcpEntityDataMover.NO_CACHE_TO_CLEAN);
		return transfered;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToCopyData) {
		boolean copied = CcpEntityDataMover.copy(this, entityToCopyData, json, CcpEntityDataMover.DIRECT_BULK, CcpEntityDataMover.NO_CACHE_TO_CLEAN);
		return copied;
	}
}
