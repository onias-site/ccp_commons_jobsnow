package com.ccp.aop;

/**
 * Raised by the null-check aspect when a method or constructor not annotated with {@link CcpAllowNullParameter}
 * receives {@code null} in one of its parameters.
 */
@SuppressWarnings("serial")
public class CcpNullParameterException extends RuntimeException {

	/**
	 * Builds the error naming the offending member and the position of the {@code null} argument.
	 * @param methodSignature signature of the method or constructor that received {@code null}
	 * @param parameterIndex zero-based index of the {@code null} argument
	 */
	public CcpNullParameterException(String methodSignature, int parameterIndex) {
		super("Null parameter detected at index [" + parameterIndex + "] in method: " + methodSignature);
	}
}
