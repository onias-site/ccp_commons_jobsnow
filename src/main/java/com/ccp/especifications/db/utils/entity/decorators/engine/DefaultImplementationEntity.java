package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Minimal base implementation of {@code CcpEntity} that wraps the metadata of an entity and
 * delegates every operation to the CRUD/bulk system through {@code CcpEntityMetaData}. It is the innermost
 * layer of the decorator chain built by {@code CcpEntityFactory}.
 */
class DefaultImplementationEntity implements CcpEntity{
	/** The metadata of the entity, not yet bound to the decorated entity. */
	final CcpEntityMetaData entityDetails;

	/** Stores the metadata of the entity to be represented. */
	public DefaultImplementationEntity(CcpEntityMetaData entityDetails) {
		this.entityDetails = entityDetails;
	}

	/**
	 * Returns the entity name.
	 * @return the entity name
	 */
	public String toString() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		return entityDetails.entityName;
	}
	
	/**
	 * Two entities are equal when they have the same name.
	 * @param obj the other object
	 * @return {@code true} for an entity with the same name
	 */
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity other) {
			CcpEntityMetaData entityDetails = this.getEntityMetaData();
			CcpEntityMetaData otherEntityDetails = other.getEntityMetaData();
			boolean sameEntityName = entityDetails.entityName.equals(otherEntityDetails.entityName);
			return sameEntityName;
		}
		return false;
	}

	/**
	 * Consistent with {@link #equals(Object)}: the hash code of the entity name.
	 * @return the hash code
	 */
	public int hashCode() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		int hashCode = entityDetails.entityName.hashCode();
		return hashCode;
	}

	/**
	 * The metadata bound to its entity, kept after the first binding that found the entity. Transient: it is a cache, not
	 * state, and serializing it (Gson, when an entity goes inside a JSON) would walk into the twin entity and its
	 * annotation proxies, which Java 17 does not let it read.
	 */
	private transient volatile CcpEntityMetaData associatedMetaData;

	/**
	 * Returns the metadata bound to its decorated entity. The binding is kept once it finds the entity: until 2026-10-06 it
	 * was rebuilt on every call, and for the twin side that meant building the whole twin entity through
	 * {@code CcpEntityFactory} on each {@code equals}, {@code hashCode} or metadata read. A binding without the entity is
	 * not kept: the main side reads the static {@code ENTITY} of the configurator, still {@code null} while that class is
	 * being initialized.
	 * @return the bound metadata
	 */
	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData cached = this.associatedMetaData;
		boolean alreadyBound = cached != null;
		if(alreadyBound) {
			return cached;
		}
		CcpEntityMetaData associatedMetaData = this.entityDetails.associateEntity();
		boolean foundTheEntity = associatedMetaData.entity != null;
		if(foundTheEntity) {
			this.associatedMetaData = associatedMetaData;
		}
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

	/**
	 * Copy in the innermost layer, for the entities without a decorator that extends the default delegator.
	 * @param json the record plus overriding fields
	 * @param entityToCopyData the target entity
	 * @return {@code false} when the record does not exist here, {@code true} otherwise
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToCopyData) {
		boolean copied = CcpEntityDataMover.copy(this, entityToCopyData, json, CcpEntityDataMover.DIRECT_BULK, CcpEntityDataMover.NO_CACHE_TO_CLEAN);
		return copied;
	}
}
