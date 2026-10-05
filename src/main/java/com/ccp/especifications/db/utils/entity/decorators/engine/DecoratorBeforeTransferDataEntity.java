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

	/** The configurator class that carries {@code @CcpEntityDataTransfers}. */
	private final Class<?> clazz;

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorBeforeTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	/**
	 * Runs {@code copyDataTo} through {@code CcpEntityDecoratorTransferType.copyDataTo.executeBefore}, which applies only the {@code before} flows.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.copyDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}

	/**
	 * Runs {@code transferDataTo} through {@code CcpEntityDecoratorTransferType.transferDataTo.executeBefore}, which applies only the {@code before} flows.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean outcome = CcpEntityDecoratorTransferType.transferDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return outcome;
	}
}
