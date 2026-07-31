package com.ccp.aop;

@SuppressWarnings("serial")
public class CcpNullParameterException extends RuntimeException {

	public CcpNullParameterException(String methodSignature, int parameterIndex) {
		super("Null parameter detected at index [" + parameterIndex + "] in method: " + methodSignature);
	}
}
