package com.ccp.decorators;

import java.lang.reflect.Method;

public class CcpReflectionMethodDecorator implements CcpDecorator<Method> {

	public final Method content;
	
	public final Object instance;

	public CcpReflectionMethodDecorator(Method content, Object instance) {
		this.instance = instance;
		this.content = content;
	}

	public Method getContent() {
		return this.content;
	}
	
	
	public String toString() {

		String name = this.content.getName();
		return name;
	}
	
	
	
}
