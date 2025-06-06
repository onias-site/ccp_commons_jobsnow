package com.ccp.decorators;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class CcpReflectionOptionsDecorator implements CcpDecorator<Class<?>> {

	public final Class<?> content;
	
	protected CcpReflectionOptionsDecorator(Class<?> clazz) {
		this.content = clazz;
	}
	
	public CcpReflectionFieldDecorator field(String fieldName) {
		try {
			Field declaredField = this.content.getDeclaredField(fieldName);
			CcpReflectionFieldDecorator decorator = new CcpReflectionFieldDecorator(declaredField, this.content);
			return decorator;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public CcpReflectionMethodDecorator fromDeclaredMethod(String methodName, Class<?>... parameters) {
		try {
			Method declaredField = this.content.getDeclaredMethod(methodName, parameters);
			CcpReflectionMethodDecorator decorator = new CcpReflectionMethodDecorator(declaredField, this.content);
			return decorator;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Class<?> getContent() {
		return this.content;
	}
	
	protected abstract Object getInstance();
}
