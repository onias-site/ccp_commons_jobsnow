package com.ccp.decorators;

/**
 * Specialization of {@code CcpReflectionOptionsDecorator} for static-context reflection: it resolves the class
 * without creating an instance.
 */
public class CcpReflectionStaticContextDecorator extends CcpReflectionOptionsDecorator {

	/**
	 * Resolves the class by name through {@code constructor.forName()}.
	 * @param constructor the decorator holding the class name
	 */
	protected CcpReflectionStaticContextDecorator(CcpReflectionConstructorDecorator constructor) {
		super(forName(constructor));
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
