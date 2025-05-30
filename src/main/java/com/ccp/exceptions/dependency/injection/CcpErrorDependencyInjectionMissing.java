package com.ccp.exceptions.dependency.injection;

@SuppressWarnings("serial")
public class CcpErrorDependencyInjectionMissing extends RuntimeException{
	
	public CcpErrorDependencyInjectionMissing(Class<?> interfaceClass) {
		super("It is missing an implementation of the interface " + interfaceClass.getName());
	}
}
