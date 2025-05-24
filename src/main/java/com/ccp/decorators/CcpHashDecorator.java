package com.ccp.decorators;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.ccp.utils.CcpHashAlgorithm;

public class CcpHashDecorator implements CcpDecorator<String> {
	public final String content;

	protected CcpHashDecorator(String content) {
		this.content = content;
	}

	public String toString() {
		return this.content;
	}

	
	public String asString(CcpHashAlgorithm algorithm) {
		BigInteger bi = this.asBigInteger(algorithm);

		String strHash = bi.toString(16).toLowerCase();

		return strHash;
	}
	
	public BigInteger asBigInteger(CcpHashAlgorithm algorithm) {
		MessageDigest digest = algorithm.getMessageDigest();
		
		byte[] bytes = this.content.getBytes(StandardCharsets.UTF_8);
		byte[] hash = digest.digest(bytes);
		BigInteger bi = new BigInteger(hash);
		return bi;
	}

	public String getContent() {
		return this.content;
	}

	
}
