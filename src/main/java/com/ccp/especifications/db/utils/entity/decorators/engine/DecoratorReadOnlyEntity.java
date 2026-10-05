package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator of the entities marked with {@code @CcpEntityOlyReadable}: {@code save}, {@code delete},
 * {@code deleteAnyWhere} and {@code transferDataTo} do nothing and return {@code false}, silently (no exception).
 * {@code copyDataTo} and {@code toBulkItems} are not blocked.
 */
class DecoratorReadOnlyEntity extends CcpEntityDelegator {

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class (unused)
	 */
	public DecoratorReadOnlyEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}

	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean delete(CcpJsonRepresentation json) {
		return false;
	}

	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		return false;
	}


	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean save(CcpJsonRepresentation json) {
		return false;
	}

	/**
	 * Transferring removes the record from here, so it is a write and stays blocked. Copying does not write to this
	 * entity and remains allowed. Until 2026-09-27 this method received {@code CcpEntity...} and did not override the
	 * interface method, so the block was never applied.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return always {@code false}
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		return false;
	}
}
