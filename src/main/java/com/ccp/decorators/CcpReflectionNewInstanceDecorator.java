package com.ccp.decorators;

public class CcpReflectionNewInstanceDecorator extends CcpReflectionOptionsDecorator {

	public final Object instance;
	
	public CcpReflectionNewInstanceDecorator(Object instance, Class<?> clazz) {
		super(clazz);
		this.instance = instance;
	}

	protected CcpReflectionNewInstanceDecorator(CcpReflectionConstructorDecorator constructor) {
		super(constructor.forName());
		this.instance = constructor.newInstance();
	}
}
