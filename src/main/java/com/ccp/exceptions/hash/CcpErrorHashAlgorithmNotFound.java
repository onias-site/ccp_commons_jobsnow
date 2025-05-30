package com.ccp.exceptions.hash;

@SuppressWarnings("serial")
public class CcpErrorHashAlgorithmNotFound extends RuntimeException{
	public CcpErrorHashAlgorithmNotFound(String algorithm) {
		super("Algorithm '" + algorithm + "' not found");

	}
}
