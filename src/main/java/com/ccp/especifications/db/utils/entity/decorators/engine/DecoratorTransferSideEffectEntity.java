package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityAnnotationAttribute;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationPhase;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityPhase;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntitySideEffect;

/**
 * Decorator of one transfer side effect annotation (such as {@code @CcpAfterTransferDataFromMainEntity}): intercepts
 * {@code transferDataTo} and {@code copyDataTo} and, when the transfer is the configured one and the entity is the
 * configured side, runs the side effect of every instance of the annotation whose {@code targetEntity} is the target.
 * The transfer annotations are repeatable, one instance per target entity. The phases behave as in
 * {@code DecoratorWriteSideEffectEntity}; the {@code after} side effect runs only when the transfer happened.
 */
class DecoratorTransferSideEffectEntity extends DecoratorSideEffectEntity {

	/** The configured transfer. */
	private final CcpEntityDecoratorTransferType transfer;

	/**
	 * Wraps the entity.
	 * @param entity the entity decorated so far
	 * @param configurationClass the configurator class
	 * @param annotationClass the annotation of this side effect
	 * @param phase when the side effect runs
	 * @param transfer the configured transfer
	 * @param entityPhase the side of the entity the annotation refers to
	 * @param sideEffect what the side effect does
	 */
	DecoratorTransferSideEffectEntity(CcpEntity entity, Class<?> configurationClass, Class<? extends Annotation> annotationClass, CcpEntityOperationPhase phase, CcpEntityDecoratorTransferType transfer, CcpEntityPhase entityPhase, CcpEntitySideEffect sideEffect) {
		super(entity, configurationClass, annotationClass, phase, entityPhase, sideEffect);
		this.transfer = transfer;
	}

	/**
	 * Runs {@code copyDataTo}, with the side effect in its phase when the configured transfer is the copy.
	 * @param json the record
	 * @param targetEntity the target entity
	 * @return the outcome of the copy; {@code false} when a handled exception canceled it
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity targetEntity) {
		boolean isAnotherTransfer = false == CcpEntityDecoratorTransferType.copyDataTo.equals(this.transfer);

		if(isAnotherTransfer) {
			return this.entity.copyDataTo(json, targetEntity);
		}

		boolean outcome = this.phase.executeTransfer(this.transfer, json, this.entity, targetEntity, this::executeSideEffectWhenConfigured);
		return outcome;
	}

	/**
	 * Runs {@code transferDataTo}, with the side effect in its phase when the configured transfer is the move.
	 * @param json the record
	 * @param targetEntity the target entity
	 * @return the outcome of the transfer; {@code false} when a handled exception canceled it
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity targetEntity) {
		boolean isAnotherTransfer = false == CcpEntityDecoratorTransferType.transferDataTo.equals(this.transfer);

		if(isAnotherTransfer) {
			return this.entity.transferDataTo(json, targetEntity);
		}

		boolean outcome = this.phase.executeTransfer(this.transfer, json, this.entity, targetEntity, this::executeSideEffectWhenConfigured);
		return outcome;
	}

	/**
	 * When the entity is the configured side, runs the side effect of every instance of the annotation whose target is the
	 * given entity, in the order they are declared, chaining their outputs.
	 * @param json the record
	 * @param targetEntity the target entity
	 * @return the JSON produced by the side effects, or the input when none applies
	 */
	private CcpJsonRepresentation executeSideEffectWhenConfigured(CcpJsonRepresentation json, CcpEntity targetEntity) {
		boolean isAnotherEntity = false == this.isTheConfiguredEntity();

		if(isAnotherEntity) {
			return json;
		}

		CcpEntityMetaData targetEntityDetails = targetEntity.getEntityMetaData();
		Annotation[] annotations = this.configurationClass.getAnnotationsByType(this.annotationClass);
		CcpJsonRepresentation result = json;
		for (Annotation annotation : annotations) {
			Class<?> configuredTargetEntity = CcpEntityAnnotationAttribute.targetEntity.readFrom(annotation);
			boolean isAnotherTarget = false == targetEntityDetails.configurationClass.equals(configuredTargetEntity);

			if(isAnotherTarget) {
				continue;
			}
			result = this.executeSideEffect(result, annotation);
		}
		return result;
	}
}
