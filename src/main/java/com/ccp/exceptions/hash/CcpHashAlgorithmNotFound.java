package com.ccp.exceptions.hash;

@SuppressWarnings("serial")
public class CcpHashAlgorithmNotFound extends RuntimeException{
	public CcpHashAlgorithmNotFound(String algorithm) {
		super("Algorithm '" + algorithm + "' not found");

	}
}
