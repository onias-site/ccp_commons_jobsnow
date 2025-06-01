package com.ccp.dependency.injection;

import java.util.HashMap;
import java.util.Map;

import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.exceptions.dependency.injection.CcpErrorDependencyInjectionMissing;

public class CcpDependencyInjection {

	static Map<Class<?>, Object> instances = new HashMap<>();
	
	public static void loadAllDependencies(CcpInstanceProvider<?>... providers) {
		
		for (CcpInstanceProvider<?> provider : providers) {
			Object implementation = provider.getInstance();
			Class<? extends Object> class1 = implementation.getClass();
			Class<?>[] interfaces = class1.getInterfaces();
			Class<?> especification = interfaces[0];
			instances.put(especification, implementation);
		}
	}
	
	public static <T>boolean hasDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		return implementation != null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		if(implementation == null) {
			throw new CcpErrorDependencyInjectionMissing(interfaceClass);
		}
		return (T) implementation;
	}
	
	public static <T> T getInstance(Class<CcpInstanceProvider<T>> interfaceClass) {
		
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(interfaceClass);
		CcpInstanceProvider<T> instanceProvider = reflection.newInstance();
		T instance = instanceProvider.getInstance();
		return instance;
	}
	
	
	
	
}
