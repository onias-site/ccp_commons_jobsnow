package com.ccp.decorators;

public abstract class CcpReflectionOptionsDecorator implements CcpDecorator<Class<?>> {

	public final Class<?> content;
	
	protected CcpReflectionOptionsDecorator(Class<?> clazz) {
		this.content = clazz;
	}
	

	public Class<?> getContent() {
		return this.content;
	}
	
}
