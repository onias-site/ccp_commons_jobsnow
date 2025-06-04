package com.ccp.decorators;

public class CcpReflectionStaticContextDecorator extends CcpReflectionOptionsDecorator {

	
	protected CcpReflectionStaticContextDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
	}
	
	protected Object getInstance() {
		return null;
	}
	
}
