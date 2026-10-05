package com.ccp.hash;


/** Raised when the JVM does not offer the requested message digest algorithm. */
@SuppressWarnings("serial")
public class CcpErrorHashAlgorithmNotFound extends RuntimeException {
	/**
	 * Builds the error naming the missing algorithm.
	 * @param algorithm the algorithm name
	 */
	CcpErrorHashAlgorithmNotFound(String algorithm) {
		super("Algorithm '" + algorithm + "' not found");
	}
}
