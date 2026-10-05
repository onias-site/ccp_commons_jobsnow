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

	/** The configurator class that carries {@code @CcpEntityDataTransfers}. */
	private final Class<?> clazz;

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorAfterTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	/**
	 * Runs {@code copyDataTo} through {@code CcpEntityDecoratorTransferType.copyDataTo.executeAfter}, which applies only the {@code after} flows.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.copyDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}

	/**
	 * Runs {@code transferDataTo} through {@code CcpEntityDecoratorTransferType.transferDataTo.executeAfter}, which applies only the {@code after} flows.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.transferDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}
}
