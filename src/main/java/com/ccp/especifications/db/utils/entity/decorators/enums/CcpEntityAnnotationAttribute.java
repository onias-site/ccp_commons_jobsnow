package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.ccp.decorators.CcpJsonFieldName;

/**
 * The attributes of the entity decorator annotations read by the decorators. Each side effect has an annotation of its
 * own (annotations cannot extend one another), so the decorators read the attributes they share by name.
 */
public enum CcpEntityAnnotationAttribute implements CcpJsonFieldName {
	/** The businesses run by a side effect. */
	execute,
	/** The exception handlers of a write side effect. */
	operationHandlers,
	/** The exception handlers of a transfer side effect. */
	transferHandlers,
	/** The only attribute of an annotation with a single parameter. */
	value,
	/** The message sent by a transfer side effect. */
	messageTemplate,
	/** The configurator class of the target entity of a transfer side effect. */
	targetEntity,
	/** The expiration granularity of a disposable entity. */
	timeOption,
	/** The decorator class that runs the {@code before} side of the writes. */
	beforeDecoratorClass,
	/** The decorator class that runs the {@code after} side of the writes. */
	afterDecoratorClass,
	;

	/**
	 * Reads this attribute from the annotation instance.
	 * @param <T> the type of the attribute
	 * @param annotation the annotation instance
	 * @return the value of the attribute
	 */
	@SuppressWarnings("unchecked")
	public <T> T readFrom(Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		String attributeName = this.name();
		Method attribute = annotationType.getMethod(attributeName);
		Object attributeValue = attribute.invoke(annotation);
		return (T) attributeValue;
	}
}
