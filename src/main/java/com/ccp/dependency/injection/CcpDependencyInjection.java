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
	 * Temporarily replaces the dependencies, runs {@code business.execute(json)} and then restores the registry exactly as
	 * it was: the previous implementation of each replaced interface comes back, and an interface that had none is left
	 * without one. The restoration happens whether the business finishes normally or throws.
	 * <p>
	 * Until 2026-10-07 the previous implementation was looked for under the interface of the provider's own class (always
	 * {@code CcpInstanceProvider}), so the method raised {@code CcpErrorDependencyInjectionMissing} before running the
	 * business (finding 57); and the restoration was not in a {@code finally}, so a business that threw left the
	 * replacements registered for the rest of the JVM (finding 2).
	 * @param json input of the business
	 * @param business the business to run while the replacements are active
	 * @param providers providers of the replacement implementations
	 * @return the result of the business
	 */
	public static CcpJsonRepresentation replaceDependenciesTemporally(CcpJsonRepresentation json, CcpBusiness business, CcpInstanceProvider<?>... providers) {

		Map<Class<?>, Object> previousImplementations = new HashMap<>();
		for (CcpInstanceProvider<?> provider : providers) {
			Object replacement = provider.getInstance();
			Class<?> especification = getEspecification(replacement);
			boolean alreadySaved = previousImplementations.containsKey(especification);
			if(alreadySaved) {
				continue;
			}
			Object previousImplementation = instances.get(especification);
			previousImplementations.put(especification, previousImplementation);
		}
		try {
			loadAllDependencies(providers);
			CcpJsonRepresentation result = business.execute(json);
			return result;
		} finally {
			restore(previousImplementations);
		}
	}

	/**
	 * Puts back the implementations saved before a temporary replacement; an interface saved without implementation is
	 * removed from the registry.
	 * @param previousImplementations the implementation of each interface before the replacement, {@code null} for none
	 */
	private static void restore(Map<Class<?>, Object> previousImplementations) {
		var entries = previousImplementations.entrySet();
		for (var entry : entries) {
			Class<?> especification = entry.getKey();
			Object previousImplementation = entry.getValue();
			boolean hadNoImplementation = previousImplementation == null;
			if(hadNoImplementation) {
				instances.remove(especification);
				continue;
			}
			instances.put(especification, previousImplementation);
		}
	}


	/**
	 * Registers every provider, mapping the first interface implemented by the instance returned by
	 * {@code getInstance()} to that instance (see {@link #getEspecification(Object)}). A later registration for the same
	 * interface replaces the earlier one.
	 * @param providers providers whose instances must be registered
	 */
	public static void loadAllDependencies(CcpInstanceProvider<?>... providers) {

		for (CcpInstanceProvider<?> provider : providers) {
			Object implementation = provider.getInstance();
			Class<?> especification = getEspecification(implementation);
			instances.put(especification, implementation);
		}
	}

	/**
	 * The interface under which the implementation is registered: the first interface declared by its class or, when the
	 * class declares none (an enum constant with a body, a subclass of an implementation), by the nearest superclass
	 * that declares one. Until 2026-10-07 only the class itself was looked at, and such an implementation raised
	 * {@code ArrayIndexOutOfBoundsException}.
	 * @param implementation the implementation
	 * @return the interface
	 * @throws CcpErrorDependencyInjectionWithoutInterface when no class of the hierarchy declares an interface
	 */
	private static Class<?> getEspecification(Object implementation) {
		Class<?> implementationClass = implementation.getClass();
		Class<?> currentClass = implementationClass;
		while(currentClass != null) {
			Class<?>[] interfaces = currentClass.getInterfaces();
			boolean declaresInterface = interfaces.length > 0;
			if(declaresInterface) {
				Class<?> especification = interfaces[0];
				return especification;
			}
			currentClass = currentClass.getSuperclass();
		}
		CcpErrorDependencyInjectionWithoutInterface error = new CcpErrorDependencyInjectionWithoutInterface(implementationClass);
		throw error;
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

	/** Raised when an implementation is registered but no class of its hierarchy declares an interface to register it under. */
	@SuppressWarnings("serial")
	public static class CcpErrorDependencyInjectionWithoutInterface extends RuntimeException {
		/**
		 * Builds the error naming the implementation class.
		 * @param implementationClass the class without interface
		 */
		private CcpErrorDependencyInjectionWithoutInterface(Class<?> implementationClass) {
			super("The implementation " + implementationClass.getName() + " declares no interface to be registered under");
		}
	}

}
