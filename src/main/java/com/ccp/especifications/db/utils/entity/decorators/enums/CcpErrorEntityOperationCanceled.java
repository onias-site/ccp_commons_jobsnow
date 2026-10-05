package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Thrown when a business of the {@code before} flow throws an exception that one of the configured
 * {@code @CcpExceptionFlow} handles: the exception was foreseen and taken care of, and the operation the
 * {@code before} flow guards must not happen. Never leaves the package: the operation catches it and returns
 * {@code false}.
 */
@SuppressWarnings("serial")
class CcpErrorEntityOperationCanceled extends RuntimeException {
	/**
	 * Wraps the handled exception.
	 * @param handledException the exception the handlers took care of
	 */
	CcpErrorEntityOperationCanceled(RuntimeException handledException) {
		super(handledException);
	}
}
