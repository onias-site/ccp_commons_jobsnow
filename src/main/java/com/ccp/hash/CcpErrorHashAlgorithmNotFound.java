package com.ccp.hash;


@SuppressWarnings("serial")
public class CcpErrorHashAlgorithmNotFound extends RuntimeException {
	CcpErrorHashAlgorithmNotFound(String algorithm) {
		super("Algorithm '" + algorithm + "' not found");
	}
}
