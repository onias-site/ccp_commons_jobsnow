package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntitySideEffect;

/**
 * The side effect that runs the businesses of the {@code execute} attribute of the annotation, in the order they are
 * declared, chaining their outputs, with the exception handlers of the annotation. In the {@code before} phase a handled
 * exception cancels the operation.
 */
public enum CcpEntitySideEffectBusinesses implements CcpEntitySideEffect, OperationWriter {
	/** Of the write annotations, whose handlers are {@code operationHandlers}. */
	operation(CcpEntityAnnotationAttribute.operationHandlers),
	/** Of the transfer annotations, whose handlers are {@code transferHandlers}. */
	transfer(CcpEntityAnnotationAttribute.transferHandlers),
	;

	/** The attribute that holds the exception handlers. */
	private final CcpEntityAnnotationAttribute handlersAttribute;

	/**
	 * Associates the item with the attribute of its handlers.
	 * @param handlersAttribute the attribute that holds the exception handlers
	 */
	private CcpEntitySideEffectBusinesses(CcpEntityAnnotationAttribute handlersAttribute) {
		this.handlersAttribute = handlersAttribute;
	}

	/**
	 * Runs the businesses of the annotation.
	 * @param json the record
	 * @param annotation the annotation instance
	 * @param phase the phase of the side effect
	 * @return the JSON produced by the last business
	 * @throws CcpErrorEntityOperationCanceled when a handled exception cancels a {@code before} side effect
	 */
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Annotation annotation, CcpEntityOperationPhase phase) {
		CcpExceptionFlow[] handlers = this.handlersAttribute.readFrom(annotation);
		Map<Class<?>, List<CcpBusiness>> exceptionHandlers = this.getExceptionHandlers(handlers);
		Class<?>[] businessClasses = CcpEntityAnnotationAttribute.execute.readFrom(annotation);
		CcpJsonRepresentation result = json;
		for (Class<?> businessClass : businessClasses) {
			CcpBusiness business = this.getBusiness(businessClass);
			result = phase.executeBusiness(this, result, business, exceptionHandlers);
		}
		return result;
	}
}
