package com.ccp.decorators;

public class CcpReflectionNewInstanceDecorator extends CcpReflectionOptionsDecorator {

	public final CcpReflectionConstructorDecorator constructor;
	
	protected CcpReflectionNewInstanceDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
		this.constructor = constructor;
	}
	
	protected Object getInstance() {
		Object newInstance = this.constructor.newInstance();
		return newInstance;
	}
	
}
