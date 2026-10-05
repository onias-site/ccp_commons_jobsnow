package com.ccp.decorators;

import java.io.InputStream;

/**
 * Decorator that reads configuration resources (JSON or {@code .properties} content). It delegates the opening of the
 * stream to {@code CcpInputStreamDecorator} and converts the result into {@code CcpJsonRepresentation}, so settings are
 * read as a uniform JSON map.
 */
public class CcpPropertiesDecorator implements CcpDecorator<CcpInputStreamDecorator> {

	/** Opens the stream of the resource. */
	private final CcpInputStreamDecorator content;

	/**
	 * Wraps the identifier of the resource.
	 * @param content the resource identifier (environment variable name, classpath resource or file path)
	 */
	protected CcpPropertiesDecorator(String content) {
		this.content = new CcpInputStreamDecorator(content);
	}

	/**
	 * Reads the stream as a JSON map.
	 * @param is the stream of the resource
	 * @return the settings
	 */
	private CcpJsonRepresentation getMapInInputStream(InputStream is) {
		CcpJsonRepresentation response = new CcpJsonRepresentation(is);
		return response;

	}
	
	/**
	 * Loads the settings from the environment variable named by the identifier.
	 * @return the settings
	 */
	public CcpJsonRepresentation environmentVariables() {
		InputStream is = this.content.environmentVariables();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Loads the settings from the classpath resource named by the identifier.
	 * @return the settings
	 */
	public CcpJsonRepresentation classLoader() {
		InputStream is = this.content.classLoader();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Loads the settings from the file system path named by the identifier.
	 * @return the settings
	 */
	public CcpJsonRepresentation file() {
		InputStream is = this.content.file();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Tries the environment variable, the classpath and the file system, in this order, and loads the settings from the
	 * first available source.
	 * @return the settings
	 */
	public CcpJsonRepresentation environmentVariablesOrClassLoaderOrFile() {
		InputStream is = this.content.fromEnvironmentVariablesOrClassLoaderOrFile();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Returns the inner stream decorator.
	 * @return the stream decorator
	 */
	public CcpInputStreamDecorator getContent() {
		return this.content;
	}
	
	
	
}
