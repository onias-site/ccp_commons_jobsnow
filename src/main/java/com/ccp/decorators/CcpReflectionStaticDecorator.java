package com.ccp.decorators;

public class CcpReflectionStaticDecorator extends CcpReflectionOptionsDecorator {

	
	protected CcpReflectionStaticDecorator(Class<?> clazz) {
		super(clazz);
	}
	
	protected Object getInstance() {
		return null;
	}
	
}
