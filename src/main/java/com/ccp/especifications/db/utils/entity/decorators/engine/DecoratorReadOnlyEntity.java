package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator that prevents any write operation on entities marked with
 * {@code @CcpEntityOlyReadable}. The methods {@code save}, {@code delete}, {@code deleteAnyWhere}
 * and {@code transferDataTo} execute nothing and return {@code false}.
 */
class DecoratorReadOnlyEntity extends CcpEntityDelegator {

	public DecoratorReadOnlyEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}

	public boolean delete(CcpJsonRepresentation json) {
		return false;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		return false;
	}


	public boolean save(CcpJsonRepresentation json) {
		return false;
	}

	/**
	 * Transferring removes the record from here, so it is a write and stays blocked. Copying does not write to this
	 * entity and remains allowed. Until 2026-09-27 this method received {@code CcpEntity...} and did not
	 * override the interface method — the block was never applied.
	
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		return false;
	}
}
