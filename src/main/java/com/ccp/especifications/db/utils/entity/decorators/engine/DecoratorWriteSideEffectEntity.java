package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationPhase;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityPhase;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntitySideEffect;

/**
 * Decorator of one write side effect annotation (such as {@code @CcpAfterSaveFromMainEntity} or
 * {@code @CcpAfterDeleteFromMainEntitySendAnEmailMessageAndIfFailsSaveAWarning}): intercepts {@code save},
 * {@code delete} and {@code deleteAnyWhere} and runs the side effect when the write (or its outcome, in the
 * {@code after} phase) is the configured one and the entity is the configured side. In the {@code before} phase it
 * stays in the outer part of the chain and the JSON it produces goes inward; in the {@code after} phase it stays in the
 * inner part and runs only after the write has actually happened.
 */
class DecoratorWriteSideEffectEntity extends DecoratorSideEffectEntity {

	/** The configured write. */
	private final CcpEntityDecoratorOperationType operation;

	/**
	 * Wraps the entity.
	 * @param entity the entity decorated so far
	 * @param configurationClass the configurator class
	 * @param annotationClass the annotation of this side effect
	 * @param phase when the side effect runs
	 * @param operation the configured write
	 * @param entityPhase the side of the entity the annotation refers to
	 * @param sideEffect what the side effect does
	 */
	DecoratorWriteSideEffectEntity(CcpEntity entity, Class<?> configurationClass, Class<? extends Annotation> annotationClass, CcpEntityOperationPhase phase, CcpEntityDecoratorOperationType operation, CcpEntityPhase entityPhase, CcpEntitySideEffect sideEffect) {
		super(entity, configurationClass, annotationClass, phase, entityPhase, sideEffect);
		this.operation = operation;
	}

	/**
	 * Runs {@code save} with the side effect in its phase.
	 * @param json the record
	 * @return the outcome of the save; {@code false} when a handled exception canceled it
	 */
	public boolean save(CcpJsonRepresentation json) {
		boolean outcome = this.phase.executeWrite(CcpEntityDecoratorOperationType.save, json, this.entity, this::executeSideEffectWhenConfigured);
		return outcome;
	}

	/**
	 * Runs {@code delete} with the side effect in its phase.
	 * @param json the record
	 * @return the outcome of the delete; {@code false} when a handled exception canceled it
	 */
	public boolean delete(CcpJsonRepresentation json) {
		boolean outcome = this.phase.executeWrite(CcpEntityDecoratorOperationType.delete, json, this.entity, this::executeSideEffectWhenConfigured);
		return outcome;
	}

	/**
	 * Runs {@code deleteAnyWhere} with the side effect in its phase.
	 * @param json the record
	 * @return the outcome of the deleteAnyWhere; {@code false} when a handled exception canceled it
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean outcome = this.phase.executeWrite(CcpEntityDecoratorOperationType.deleteAnyWhere, json, this.entity, this::executeSideEffectWhenConfigured);
		return outcome;
	}

	/**
	 * Runs the side effect when the configured write covers the one that happens (in the {@code after} phase, its
	 * outcome) and the entity is the configured side.
	 * @param json the record
	 * @param executed the write that happens, or its outcome
	 * @return the JSON produced by the side effect, or the input when it does not apply
	 */
	private CcpJsonRepresentation executeSideEffectWhenConfigured(CcpJsonRepresentation json, CcpEntityDecoratorOperationType executed) {
		boolean isAnotherOperation = false == this.operation.covers(executed);

		if(isAnotherOperation) {
			return json;
		}

		boolean isAnotherEntity = false == this.isTheConfiguredEntity();

		if(isAnotherEntity) {
			return json;
		}

		Annotation annotation = this.configurationClass.getAnnotation(this.annotationClass);
		CcpJsonRepresentation result = this.executeSideEffect(json, annotation);
		return result;
	}
}
