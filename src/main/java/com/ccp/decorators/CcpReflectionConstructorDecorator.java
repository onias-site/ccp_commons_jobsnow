package com.ccp.decorators;

import java.lang.reflect.Constructor;

/**
 * Decorator over the fully qualified name of a Java class, offering reflection operations: class resolution, instance
 * creation and choice of the invocation context (static or instance). It is the entry point of the framework's fluent
 * reflection API.
 */
public class CcpReflectionConstructorDecorator implements CcpDecorator<String> {

	/** The fully qualified class name. */
	public final String content;

	/**
	 * Wraps the fully qualified class name.
	 * @param content the class name
	 */
	protected CcpReflectionConstructorDecorator(String content) {
		this.content = content;
	}

	/**
	 * Reads the class name from a field of the JSON.
	 * @param json the JSON holding the class name
	 * @param field the field holding the class name
	 */
	public CcpReflectionConstructorDecorator(CcpJsonRepresentation json, String field) {
		CcpFieldName ccpFieldName = new CcpFieldName(field);
		this.content = json.getAsString(ccpFieldName);
	}

	/**
	 * Takes the class name from a {@code Class} object.
	 * @param clazz the class
	 */
	public CcpReflectionConstructorDecorator(Class<?> clazz) {
		this.content = clazz.getName();
	}

	/**
	 * Loads the class by name.
	 * @return the loaded class
	 * @throws org.aspectj.lang.SoftException wrapping {@code ClassNotFoundException} when the class does not exist
	 */
	public Class<?> forName(){
		Class<?> forName = Class.forName(this.content);
		return forName;
	}

	/**
	 * Tells whether the class can be loaded, without throwing.
	 * @return {@code true} when the class exists
	 */
	public boolean thisClassExists(){
		try {
			Class.forName(this.content);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Creates a new instance of the class through its no-arg constructor, even when that constructor is private
	 * (it uses {@code setAccessible(true)}).
	 * @param <T> the expected type of the instance
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		Class<?> forName = Class.forName(this.content);
		Constructor<?> declaredConstructor = forName.getDeclaredConstructor();
		declaredConstructor.setAccessible(true);
		var newInstance2 = declaredConstructor.newInstance();
		T newInstance = (T) newInstance2;
		return newInstance;

	}

	/**
	 * Returns the class name.
	 * @return the class name
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Returns the fully qualified class name.
	 * @return the class name
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Starts a static-context reflection, without creating an instance.
	 * @return a {@code CcpReflectionStaticContextDecorator}
	 */
	public CcpReflectionOptionsDecorator fromStaticContext() {
		CcpReflectionStaticContextDecorator result = new CcpReflectionStaticContextDecorator(this);
		return result;
	}

	/**
	 * Starts an instance reflection over a new instance of the class.
	 * @return a {@code CcpReflectionNewInstanceDecorator} holding the new instance
	 */
	public CcpReflectionOptionsDecorator fromNewInstance() {
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(this);
		return result;
	}

	/**
	 * Starts an instance reflection over an existing instance (its own class is used, not the wrapped name).
	 * @param instance the existing instance
	 * @return a {@code CcpReflectionNewInstanceDecorator} holding the instance
	 */
	public CcpReflectionOptionsDecorator fromInstance(Object instance) {
		Class<?> clazz = instance.getClass();
		CcpReflectionOptionsDecorator result = new CcpReflectionNewInstanceDecorator(instance, clazz);
		return result;
	}

}
