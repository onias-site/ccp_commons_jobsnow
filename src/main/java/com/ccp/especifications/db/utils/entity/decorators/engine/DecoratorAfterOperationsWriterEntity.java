package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;

/**
 * Decorator that intercepts the write operations ({@code save}, {@code delete},
 * {@code deleteAnyWhere}) to execute only the {@code after} flows configured in
 * {@code @CcpEntityOperations}. It stays in the inner part of the chain (low priority) so that the subsequent side
 * effects only happen after the write has actually taken place. The
 * {@code before} flow is the responsibility of {@code DecoratorBeforeOperationsWriterEntity}.
 */
class DecoratorAfterOperationsWriterEntity extends CcpEntityDelegator {

	/** The configurator class that carries {@code @CcpEntityOperations}. */
	private final Class<?> clazz;

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorAfterOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	/**
	 * Runs {@code save} through {@code CcpEntityDecoratorOperationType.save.executeAfter}, which applies only the {@code after} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean save(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.save.executeAfter(json, this.clazz, this.entity);
		return outcome;
	}

	/**
	 * Runs {@code delete} through {@code CcpEntityDecoratorOperationType.delete.executeAfter}, which applies only the {@code after} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean delete(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.delete.executeAfter(json, this.clazz, this.entity);
		return outcome;
	}

	/**
	 * Runs {@code deleteAnyWhere} through {@code CcpEntityDecoratorOperationType.deleteAnyWhere.executeAfter}, which applies only the {@code after} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.deleteAnyWhere.executeAfter(json, this.clazz, this.entity);
		return outcome;
	}
}
