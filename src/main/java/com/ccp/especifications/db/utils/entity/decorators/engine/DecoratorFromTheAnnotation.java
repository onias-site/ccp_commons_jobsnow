package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityAnnotationAttribute;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Builds the decorators whose implementation is a collaborator named by the annotation itself (versionable, purge of
 * the history, asynchronous writer, disposable, grouper child), the way {@code @CcpEntityTwin} names its bulk executor:
 * the engine decides when the decorator enters the chain, the class given to the annotation decides what it does.
 */
enum DecoratorFromTheAnnotation {
	/** The implementation is the {@code value} of the annotation, with a constructor receiving the entity. */
	value{
		/**
		 * Instantiates the {@code value} of the annotation with the entity.
		 * @param entity the entity decorated so far
		 * @param annotation the annotation instance
		 * @return the decorated entity
		 */
		CcpEntity getEntity(CcpEntity entity, Annotation annotation) {
			Class<?> decoratorClass = CcpEntityAnnotationAttribute.value.readFrom(annotation);
			Constructor<?> constructor = decoratorClass.getConstructor(CcpEntity.class);
			Object decoratedEntity = constructor.newInstance(entity);
			return (CcpEntity) decoratedEntity;
		}
	},
	/**
	 * The implementation is the {@code value} of the annotation, with a constructor receiving the entity and the
	 * {@code timeOption} of the annotation.
	 */
	valueWithTimeOption{
		/**
		 * Instantiates the {@code value} of the annotation with the entity and the expiration granularity.
		 * @param entity the entity decorated so far
		 * @param annotation the annotation instance
		 * @return the decorated entity
		 */
		CcpEntity getEntity(CcpEntity entity, Annotation annotation) {
			Class<?> decoratorClass = CcpEntityAnnotationAttribute.value.readFrom(annotation);
			CcpEntityExpurgableOptions timeOption = CcpEntityAnnotationAttribute.timeOption.readFrom(annotation);
			Constructor<?> constructor = decoratorClass.getConstructor(CcpEntity.class, CcpEntityExpurgableOptions.class);
			Object decoratedEntity = constructor.newInstance(entity, timeOption);
			return (CcpEntity) decoratedEntity;
		}
	},
	/** The implementation is the {@code beforeDecoratorClass} of the annotation, receiving the entity and the annotation. */
	beforeDecoratorClass{
		/**
		 * Instantiates the {@code beforeDecoratorClass} of the annotation with the entity and the annotation.
		 * @param entity the entity decorated so far
		 * @param annotation the annotation instance
		 * @return the decorated entity
		 */
		CcpEntity getEntity(CcpEntity entity, Annotation annotation) {
			Class<?> decoratorClass = CcpEntityAnnotationAttribute.beforeDecoratorClass.readFrom(annotation);
			CcpEntity decoratedEntity = instantiateWithTheAnnotation(decoratorClass, entity, annotation);
			return decoratedEntity;
		}
	},
	/** The implementation is the {@code afterDecoratorClass} of the annotation, receiving the entity and the annotation. */
	afterDecoratorClass{
		/**
		 * Instantiates the {@code afterDecoratorClass} of the annotation with the entity and the annotation.
		 * @param entity the entity decorated so far
		 * @param annotation the annotation instance
		 * @return the decorated entity
		 */
		CcpEntity getEntity(CcpEntity entity, Annotation annotation) {
			Class<?> decoratorClass = CcpEntityAnnotationAttribute.afterDecoratorClass.readFrom(annotation);
			CcpEntity decoratedEntity = instantiateWithTheAnnotation(decoratorClass, entity, annotation);
			return decoratedEntity;
		}
	},
	;

	/**
	 * Wraps the entity with the implementation named by the annotation.
	 * @param entity the entity decorated so far
	 * @param annotation the annotation instance
	 * @return the decorated entity
	 */
	abstract CcpEntity getEntity(CcpEntity entity, Annotation annotation);

	/**
	 * Instantiates a decorator class whose constructor receives the entity and the annotation.
	 * @param decoratorClass the decorator class
	 * @param entity the entity decorated so far
	 * @param annotation the annotation instance
	 * @return the decorated entity
	 */
	private static CcpEntity instantiateWithTheAnnotation(Class<?> decoratorClass, CcpEntity entity, Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		Constructor<?> constructor = decoratorClass.getConstructor(CcpEntity.class, annotationType);
		Object decoratedEntity = constructor.newInstance(entity, annotation);
		return (CcpEntity) decoratedEntity;
	}
}
