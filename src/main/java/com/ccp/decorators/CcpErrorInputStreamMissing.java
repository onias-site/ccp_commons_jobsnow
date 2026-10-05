package com.ccp.decorators;


/** Raised when no input stream can be opened for the requested resource (environment variable, classpath or file). */
@SuppressWarnings("serial")
public class CcpErrorInputStreamMissing extends RuntimeException {
	/**
	 * Builds the error naming the missing resource.
	 * @param filePath the identifier of the missing resource
	 */
	CcpErrorInputStreamMissing(String filePath) {
		super("The file '" + filePath + "' is missing");
	}
}
