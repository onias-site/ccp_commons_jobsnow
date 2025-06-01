package com.ccp.decorators;

import java.lang.reflect.Field;

public class CcpReflectionFieldDecorator implements CcpDecorator<Field> {

	public final Field content;
	
	public final Object instance;

	public CcpReflectionFieldDecorator(Field content, Object instance) {
		this.instance = instance;
		this.content = content;
	}

	public Field getContent() {
		return this.content;
	}
	
	
	public String toString() {

		String name = this.content.getName();
		return name;
	}
	
	
	
}
