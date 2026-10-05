package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;

/**
 * Decorator that intercepts the write operations ({@code save}, {@code delete},
 * {@code deleteAnyWhere}) to execute only the {@code before} flows configured in
 * {@code @CcpEntityOperations}. It stays in the outer part of the chain (high priority) so that the preceding side
 * effects happen before the other decorators and the JSON they produce is the one that goes
 * inward. The {@code after} flow is the responsibility of {@code DecoratorAfterOperationsWriterEntity}.
 */
class DecoratorBeforeOperationsWriterEntity extends CcpEntityDelegator {

	/** The configurator class that carries {@code @CcpEntityOperations}. */
	private final Class<?> clazz;

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorBeforeOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	/**
	 * Runs {@code save} through {@code CcpEntityDecoratorOperationType.save.executeBefore}, which applies only the {@code before} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean save(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.save.executeBefore(json, this.clazz, this.entity);
		return outcome;
	}

	/**
	 * Runs {@code delete} through {@code CcpEntityDecoratorOperationType.delete.executeBefore}, which applies only the {@code before} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean delete(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.delete.executeBefore(json, this.clazz, this.entity);
		return outcome;
	}

	/**
	 * Runs {@code deleteAnyWhere} through {@code CcpEntityDecoratorOperationType.deleteAnyWhere.executeBefore}, which applies only the {@code before} flows.
	 * @param json the record
	 * @return the outcome of the operation; {@code false} when a handled exception canceled it
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean outcome = CcpEntityDecoratorOperationType.deleteAnyWhere.executeBefore(json, this.clazz, this.entity);
		return outcome;
	}
}
