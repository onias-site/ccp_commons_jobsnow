package com.ccp.dependency.injection;

import java.util.HashMap;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;

/**
 * Central dependency injection registry of the framework. Keeps a static map from interface to implementation and
 * offers methods to register, retrieve, remove and temporarily replace implementations. It is the mechanism that
 * allows swapping behaviors (real vs. mock) in tests or in different environments.
 */
public class CcpDependencyInjection {

	/** Registered implementations, keyed by the interface they implement. Not synchronized: dependencies are expected to be loaded at startup. */
	static Map<Class<?>, Object> instances = new HashMap<>();

	/**
	 * Temporarily replaces the dependencies, runs {@code business.execute(json)} and then restores the implementations
	 * that were registered before.
	 * <p>
	 * The restoration only happens when the business finishes normally: if it throws, the replacement stays registered.
	 * Each provider must have the target interface as the first interface of its own class.
	 * @param json input of the business
	 * @param business the business to run while the replacements are active
	 * @param providers providers of the replacement implementations
	 * @return the result of the business
	 */
	@SuppressWarnings("rawtypes")
	public static CcpJsonRepresentation replaceDependenciesTemporally(CcpJsonRepresentation json, CcpBusiness business, CcpInstanceProvider<?>... providers) {

		CcpInstanceProvider[] actuallyDependecies = new CcpInstanceProvider[providers.length];
		int k = 0;
		for (CcpInstanceProvider<?> provider : providers) {
			var providerClass = provider.getClass();
			var interfaces2 = providerClass.getInterfaces();
			actuallyDependecies[k++] = (CcpInstanceProvider) getDependency(interfaces2[0]);
		}
		loadAllDependencies(providers);

		CcpJsonRepresentation apply = business.execute(json);
		loadAllDependencies(actuallyDependecies);
		return apply;
	}
 

	/**
	 * Registers every provider, mapping the first interface implemented by the instance returned by
	 * {@code getInstance()} to that instance. A later registration for the same interface replaces the earlier one.
	 * @param providers providers whose instances must be registered
	 */
	public static void loadAllDependencies(CcpInstanceProvider<?>... providers) {

		for (CcpInstanceProvider<?> provider : providers) {
			Object implementation = provider.getInstance();
			Class<? extends Object> class1 = implementation.getClass();
			Class<?>[] interfaces = class1.getInterfaces();
			Class<?> especification = interfaces[0];
			instances.put(especification, implementation);
		}
	}

	/**
	 * Tells whether there is an implementation registered for the interface.
	 * @param interfaceClass the interface
	 * @return {@code true} when an implementation is registered
	 */
	public static <T> boolean hasDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		boolean implementationFound = implementation != null;
		return implementationFound;
	}

	/**
	 * Retrieves the implementation registered for the interface.
	 * @param interfaceClass the interface
	 * @return the registered implementation
	 * @throws CcpErrorDependencyInjectionMissing when no implementation was registered
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDependency(Class<T> interfaceClass) {
		Object implementation = instances.get(interfaceClass);
		boolean implementationMissing = implementation == null;
		if(implementationMissing) {
			CcpErrorDependencyInjectionMissing ccpErrorDependencyInjectionMissing = new CcpErrorDependencyInjectionMissing(interfaceClass);
			throw ccpErrorDependencyInjectionMissing;
		}
		T t = (T) implementation;
		return t;
	}
	
	/** Removes every registered implementation. */
	public static void removeAllDependencies() {
		instances.clear();
	}

	/**
	 * Removes the implementation registered for the interface, if any.
	 * @param interfaceClass the interface
	 */
	public static void removeDependecy(Class<?> interfaceClass) {
		instances.remove(interfaceClass);
	}

	/**
	 * Instantiates the provider class by reflection (no-arg constructor), calls {@code getInstance()} and returns the
	 * resulting object, without registering it.
	 * @param interfaceClass the provider class
	 * @return the instance built by the provider
	 */
	public static <T> T getInstance(Class<CcpInstanceProvider<T>> interfaceClass) {

		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(interfaceClass);
		CcpInstanceProvider<T> instanceProvider = reflection.newInstance();
		T instance = instanceProvider.getInstance();
		return instance;
	}

	/** Raised when an implementation is requested for an interface that has none registered. */
	@SuppressWarnings("serial")
	public static class CcpErrorDependencyInjectionMissing extends RuntimeException {
		/**
		 * Builds the error naming the interface that has no implementation.
		 * @param interfaceClass the interface without implementation
		 */
		private CcpErrorDependencyInjectionMissing(Class<?> interfaceClass) {
			super("It is missing an implementation of the interface " + interfaceClass.getName());
		}
	}

}
