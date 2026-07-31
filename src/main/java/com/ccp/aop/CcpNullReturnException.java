package com.ccp.aop;

@SuppressWarnings("serial")
public class CcpNullReturnException extends RuntimeException {

	public CcpNullReturnException(String methodSignature) {
		super("Null return detected in method: " + methodSignature);
	}
}
