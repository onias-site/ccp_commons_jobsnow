package com.ccp.decorators;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;


/**
 * Decorator over a resource identifier (environment variable name, file path or classpath resource) that opens an
 * {@code InputStream} from different sources, with automatic fallback between them.
 */
public class CcpInputStreamDecorator implements CcpDecorator<String> {

	/** The resource identifier. */
	private final String content;

	/**
	 * Wraps the resource identifier.
	 * @param content the name or path of the resource
	 */
	protected CcpInputStreamDecorator(String content) {
		this.content = content;
	}
	
	/**
	 * Returns the resource identifier.
	 * @return the resource identifier
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Reads the environment variable named by the identifier. If its value is the path of an existing file, the file is
	 * opened; otherwise the value itself becomes the stream content (platform default charset).
	 * @return the stream of the file or of the variable value
	 * @throws CcpErrorInputStreamMissing when the variable does not exist or is blank
	 */
	public InputStream environmentVariables() {
		
		String getenv = System.getenv(this.content);
		boolean environmentVariableIsMissing = getenv == null;

		if(environmentVariableIsMissing) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing;
		}
		String getenvTrim = getenv.trim();
		boolean getenvTrimEmpty = getenvTrim.isEmpty();

		if(getenvTrimEmpty) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing2 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing2;
		}

		CcpStringDecorator csd = new CcpStringDecorator(getenv);
		CcpFileDecorator file = csd.file();
		boolean fileFile = file.isFile();

		if(fileFile) {
			CcpInputStreamDecorator inputStreamFrom = csd.inputStreamFrom();
			InputStream file2 = inputStreamFrom.file();
			return file2;
		}
		
		byte[] bytes = getenv.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	/**
	 * Opens the resource from the classpath (through {@code ClassLoader.getResource}).
	 * @return the stream of the resource
	 * @throws CcpErrorInputStreamMissing when the resource is not found
	 */
	public InputStream classLoader() {
		Class<? extends CcpInputStreamDecorator> class1 = this.getClass();
		ClassLoader classLoader = class1.getClassLoader();
		URL resource = classLoader.getResource(this.content);
		boolean resourceIsMissing = resource == null;
		if(resourceIsMissing) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing3 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing3;
		}
		InputStream stream = resource.openStream(); 
		return stream;
	} 
	
	/**
	 * Opens the file system file whose path is the identifier.
	 * @return the stream of the file
	 * @throws CcpErrorInputStreamMissing when the file does not exist
	 */
	public InputStream file() {
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(this.content);
		CcpFileDecorator file = ccpStringDecorator.file();
		boolean exists = file.exists();
		boolean notExists = false == exists;
		if(notExists) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing4 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing4;
		}
		FileInputStream fileInputStream = new FileInputStream(this.content);
		return fileInputStream;
	}
	
	/**
	 * Turns the identifier text itself into a stream (platform default charset).
	 * @return a stream over the identifier text
	 */
	public InputStream byteArray() {
		byte[] bytes = this.content.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	/**
	 * Tries the three sources in order (environment variable, classpath, file) and returns the first stream that opens.
	 * @return the stream of the first available source
	 * @throws CcpErrorInputStreamMissing when none of the sources has the resource
	 */
	public InputStream fromEnvironmentVariablesOrClassLoaderOrFile() {

		try {
			InputStream is = this.environmentVariables();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		
		try {
			InputStream is = this.classLoader();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		InputStream is = this.file();
		return is;
	}

	/**
	 * Returns the resource identifier.
	 * @return the resource identifier
	 */
	public String getContent() {
		return this.content;
	}

}
