package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationPhase;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityPhase;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntitySideEffect;

/**
 * Base of the decorators of one side effect annotation: knows the annotation, the phase it runs in, the side of the
 * entity it refers to and what it does. Each side effect annotation of the configurator class is a decorator of its own
 * in the chain, at the position of its phase.
 */
abstract class DecoratorSideEffectEntity extends CcpEntityDelegator {

	/** The configurator class that carries the annotation. */
	protected final Class<?> configurationClass;

	/** The annotation of this side effect. */
	protected final Class<? extends Annotation> annotationClass;

	/** When the side effect runs: before or after the operation. */
	protected final CcpEntityOperationPhase phase;

	/** The side of the entity the annotation refers to: main or twin. */
	private final CcpEntityPhase entityPhase;

	/** What the side effect does. */
	private final CcpEntitySideEffect sideEffect;

	/**
	 * Wraps the entity.
	 * @param entity the entity decorated so far
	 * @param configurationClass the configurator class
	 * @param annotationClass the annotation of this side effect
	 * @param phase when the side effect runs
	 * @param entityPhase the side of the entity the annotation refers to
	 * @param sideEffect what the side effect does
	 */
	protected DecoratorSideEffectEntity(CcpEntity entity, Class<?> configurationClass, Class<? extends Annotation> annotationClass, CcpEntityOperationPhase phase, CcpEntityPhase entityPhase, CcpEntitySideEffect sideEffect) {
		super(entity);
		this.configurationClass = configurationClass;
		this.annotationClass = annotationClass;
		this.phase = phase;
		this.entityPhase = entityPhase;
		this.sideEffect = sideEffect;
	}

	/**
	 * Tells whether the entity of this chain is the side the annotation refers to: the same configurator class builds
	 * both the main entity and its twin.
	 * @return {@code true} when the annotation refers to this entity
	 */
	protected boolean isTheConfiguredEntity() {
		String configuredEntityName = this.entityPhase.extractEntityName(this.configurationClass);
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		boolean isTheConfiguredEntity = configuredEntityName.equals(entityDetails.entityName);
		return isTheConfiguredEntity;
	}

	/**
	 * Runs the side effect configured by one instance of the annotation.
	 * @param json the record
	 * @param annotation the annotation instance
	 * @return the JSON produced by the side effect
	 */
	protected CcpJsonRepresentation executeSideEffect(CcpJsonRepresentation json, Annotation annotation) {
		CcpJsonRepresentation result = this.sideEffect.execute(json, annotation, this.phase);
		return result;
	}
}
