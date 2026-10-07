package com.ccp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Catalog of the hash algorithms supported by the framework (MD5, SHA1, SHA256, SHA512). Holds the technical name
 * of each algorithm and keeps one {@code MessageDigest} per algorithm and per thread, so they are not created again and
 * are never shared between threads.
 */
public enum CcpHashAlgorithm {
	/** MD5 (128 bits). */
	MD5("MD5"),
	/** SHA-1 (160 bits); used to compute entity record ids. */
	SHA1("SHA1"),
	/** SHA-256 (256 bits). */
	SHA256("SHA-256"),
	/** SHA-512 (512 bits). */
	SHA512("SHA-512")
	;
	/** Algorithm name as understood by {@code MessageDigest.getInstance}. */
	private final String algorithm;

	/**
	 * Associates the constant with its JVM algorithm name.
	 * @param algorithm the JVM algorithm name
	 */
	private CcpHashAlgorithm(String algorithm) {
		this.algorithm = algorithm;
		this.messageDigests = ThreadLocal.withInitial(() -> getMessageDigest(algorithm));
	}

	/**
	 * One digest of this algorithm per thread: a {@code MessageDigest} keeps the state of the hash being computed, so two
	 * threads must never share one. Until 2026-10-06 a single instance per algorithm, in a static {@code HashMap}, was
	 * shared by every thread, and concurrent requests could mix their bytes and produce wrong entity ids.
	 */
	private final ThreadLocal<MessageDigest> messageDigests;

	/**
	 * Returns the {@code MessageDigest} of this algorithm owned by the current thread, reset and ready for a new hash.
	 * @return the digest of the current thread
	 */
	public 	MessageDigest getMessageDigest() {
		MessageDigest messageDigest = this.messageDigests.get();
		messageDigest.reset();
		return messageDigest;
	}

	/**
	 * Creates a new {@code MessageDigest} for an algorithm given by name.
	 * @param algorithm the algorithm name (e.g. "SHA1", "MD5")
	 * @return a new digest instance
	 * @throws CcpErrorHashAlgorithmNotFound when the JVM does not recognize the algorithm
	 */
	public static MessageDigest getMessageDigest(String algorithm) {
		MessageDigest instance;
		try {
			instance = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			CcpErrorHashAlgorithmNotFound ccpErrorHashAlgorithmNotFound = new CcpErrorHashAlgorithmNotFound(algorithm);
			throw ccpErrorHashAlgorithmNotFound;
		}
		return instance;
	}


}
