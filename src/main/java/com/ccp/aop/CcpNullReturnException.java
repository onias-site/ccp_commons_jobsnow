package com.ccp.aop;

/** Raised by the null-check aspect when a method not annotated with {@link CcpAllowNullReturn} returns {@code null}. */
@SuppressWarnings("serial")
public class CcpNullReturnException extends RuntimeException {

	/**
	 * Builds the error naming the method that returned {@code null}.
	 * @param methodSignature signature of the method that returned {@code null}
	 */
	public CcpNullReturnException(String methodSignature) {
		super("Null return detected in method: " + methodSignature);
	}
}
