package com.ccp.decorators;

/**
 * Base class of the framework's reflection hierarchy. It wraps the target {@code Class<?>}; the subclasses
 * ({@code CcpReflectionNewInstanceDecorator} and {@code CcpReflectionStaticContextDecorator}) specialize it for
 * instance or static-context use.
 */
public abstract class CcpReflectionOptionsDecorator implements CcpDecorator<Class<?>> {

	/** The wrapped class. */
	public final Class<?> content;

	/**
	 * Wraps the class.
	 * @param clazz the class to wrap
	 */
	protected CcpReflectionOptionsDecorator(Class<?> clazz) {
		this.content = clazz;
	}

	/**
	 * Returns the wrapped class.
	 * @return the wrapped class
	 */
	public Class<?> getContent() {
		return this.content;
	}

}
