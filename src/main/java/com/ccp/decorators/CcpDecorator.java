package com.ccp.decorators;

/**
 * Base contract of the framework's decorator pattern. Every wrapper of a specific type implements it, so the wrapped
 * content can be retrieved with its type.
 * @param <T> the wrapped type
 */
public interface CcpDecorator<T> {

	/**
	 * Returns the object wrapped by the decorator.
	 * @return the wrapped object
	 */
	T getContent();

}
