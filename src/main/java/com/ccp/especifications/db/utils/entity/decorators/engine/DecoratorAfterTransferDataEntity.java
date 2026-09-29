package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;

/**
 * Decorator that intercepts the data transfer operations ({@code copyDataTo} and
 * {@code transferDataTo}) to execute only the {@code after} flows configured in
 * {@code @CcpEntityDataTransfers}. It stays in the inner part of the chain (low priority) so that the subsequent side
 * effects only happen after the transfer has actually taken place. The
 * {@code before} flow is the responsibility of {@code DecoratorBeforeTransferDataEntity}.
 */
class DecoratorAfterTransferDataEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorAfterTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.copyDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.transferDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}
}
