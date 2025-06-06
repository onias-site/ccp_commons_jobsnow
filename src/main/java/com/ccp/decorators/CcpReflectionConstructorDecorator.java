package com.ccp.decorators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class CcpReflectionConstructorDecorator implements CcpDecorator<String> {

	public final String content;

	protected CcpReflectionConstructorDecorator(String content) {
		this.content = content;
	}

	public CcpReflectionConstructorDecorator(CcpJsonRepresentation json, String field) {
		this.content = json.getAsString(field);
	}

	public CcpReflectionConstructorDecorator(Class<?> clazz) {
		this.content = clazz.getName();
	}
	
	public Class<?> forName(){
		try {
			Class<?> forName = Class.forName(this.content);
			return forName;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean thisClassExists(){
		try {
			Class.forName(this.content);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		try {
			Class<?> forName = Class.forName(this.content);
			Constructor<?> declaredConstructor = forName.getDeclaredConstructor();
			declaredConstructor.setAccessible(true);
			T newInstance = (T) declaredConstructor.newInstance();
			return newInstance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	}
	
	public String getContent() {
		return this.content;
	}

	public String toString() {
		return this.content;
	}
	
	
	public CcpReflectionOptionsDecorator fromStaticContext() {
		CcpReflectionStaticContextDecorator result = new CcpReflectionStaticContextDecorator(this);
		return result;
	} 

	public CcpReflectionOptionsDecorator fromNewInstance() {
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(this);
		return result;
	} 

	public CcpReflectionOptionsDecorator fromAnnotationInstance(Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(annotation, annotationType);
		return result;
	}

	public CcpReflectionOptionsDecorator fromInstance(Object instance) {
		Class<?> clazz = instance.getClass();
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(instance, clazz);
		return result;
	} 
	
}
