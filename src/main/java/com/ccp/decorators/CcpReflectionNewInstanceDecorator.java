package com.ccp.decorators;

/**
 * Specialization of {@code CcpReflectionOptionsDecorator} that keeps an object instance for instance-method calls
 * through reflection. It can wrap an existing instance or create a new one through
 * {@code CcpReflectionConstructorDecorator}.
 */
public class CcpReflectionNewInstanceDecorator extends CcpReflectionOptionsDecorator {

	/** The wrapped instance. */
	public final Object instance;

	/**
	 * Wraps an existing instance and its class.
	 * @param instance the instance
	 * @param clazz the class of the instance
	 */
	public CcpReflectionNewInstanceDecorator(Object instance, Class<?> clazz) {
		super(clazz);
		this.instance = instance;
	}

	/**
	 * Loads the class and creates a new instance of it through its no-arg constructor.
	 * @param constructor the decorator holding the class name
	 */
	protected CcpReflectionNewInstanceDecorator(CcpReflectionConstructorDecorator constructor) {
		super(forName(constructor));
		this.instance = constructor.newInstance();
	}

	/**
	 * Resolves the class named by the constructor decorator.
	 * @param constructor the decorator holding the class name
	 * @return the loaded class
	 */
	private static Class<?> forName(CcpReflectionConstructorDecorator constructor) {
		var forName = constructor.forName();
		return forName;
	}
}
