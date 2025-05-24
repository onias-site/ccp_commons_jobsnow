package com.ccp.exceptions.dependency.injection;

@SuppressWarnings("serial")
public class CcpDependencyInjectionMissing extends RuntimeException{
	
	public CcpDependencyInjectionMissing(Class<?> interfaceClass) {
		super("It is missing an implementation of the interface " + interfaceClass.getName());
	}
}
