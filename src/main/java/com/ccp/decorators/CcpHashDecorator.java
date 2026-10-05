package com.ccp.decorators;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.ccp.hash.CcpHashAlgorithm;

/**
 * Decorator over a text that offers cryptographic hashes (MD5, SHA1, SHA256, SHA512) of its UTF-8 bytes, returned as
 * {@code BigInteger} or as hexadecimal text.
 */
public class CcpHashDecorator implements CcpDecorator<String> {
	/** The text to be hashed. */
	public final String content;

	/**
	 * Wraps the text to be hashed.
	 * @param content the text to be hashed
	 */
	protected CcpHashDecorator(String content) {
		this.content = content;
	}

	/**
	 * Returns the original text.
	 * @return the original text
	 */
	public String toString() {
		return this.content;
	}

	
	/**
	 * Applies the hash algorithm and returns the digest as lowercase hexadecimal text.
	 * <p>
	 * The text comes from the signed {@code BigInteger} of the digest: it starts with {@code "-"} when the first bit of the
	 * digest is set, and leading zeros are dropped, so its length varies. Record ids are computed this way, so the format
	 * is a persisted contract.
	 * @param algorithm the hash algorithm
	 * @return the hexadecimal text of the digest
	 */
	public String asString(CcpHashAlgorithm algorithm) {
		BigInteger bi = this.asBigInteger(algorithm);
		String toString = bi.toString(16);

		String strHash = toString.toLowerCase();

		return strHash;
	}
	
	/**
	 * Applies the hash algorithm to the UTF-8 bytes of the text and returns the digest as a signed {@code BigInteger}.
	 * @param algorithm the hash algorithm
	 * @return the digest as a signed number
	 */
	public BigInteger asBigInteger(CcpHashAlgorithm algorithm) {
		MessageDigest digest = algorithm.getMessageDigest();
		
		byte[] bytes = this.content.getBytes(StandardCharsets.UTF_8);
		byte[] hash = digest.digest(bytes);
		BigInteger bi = new BigInteger(hash);
		return bi;
	}

	/**
	 * Returns the original text.
	 * @return the original text
	 */
	public String getContent() {
		return this.content;
	}

}
