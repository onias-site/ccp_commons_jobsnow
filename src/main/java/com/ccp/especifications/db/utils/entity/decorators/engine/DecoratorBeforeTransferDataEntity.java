package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;

/**
 * Decorator that intercepts the data transfer operations ({@code copyDataTo} and
 * {@code transferDataTo}) to execute only the {@code before} flows configured in
 * {@code @CcpEntityDataTransfers}. It stays in the outer part of the chain (high priority) so that the preceding side
 * effects happen before the other decorators and the JSON they produce is the one that goes
 * inward. The {@code after} flow is the responsibility of {@code DecoratorAfterTransferDataEntity}.
 */
class DecoratorBeforeTransferDataEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorBeforeTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.copyDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.transferDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}
}
