package com.ccp.decorators;

public class CcpReflectionInstanceDecorator extends CcpReflectionOptionsDecorator {

	public final CcpReflectionConstructorDecorator constructor;
	
	protected CcpReflectionInstanceDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
		this.constructor = constructor;
	}
	
	protected Object getInstance() {
		Object newInstance = this.constructor.newInstance();
		return newInstance;
	}
	
}
