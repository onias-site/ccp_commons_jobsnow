package com.ccp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Catalog of the hash algorithms supported by the framework (MD5, SHA1, SHA256, SHA512). Holds the technical name
 * of each algorithm and keeps a cache of {@code MessageDigest} instances to avoid creating them again.
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
	}
	
	/** Cache of digest instances per algorithm. Neither the map nor the cached {@code MessageDigest} is thread-safe. */
	private static HashMap<CcpHashAlgorithm, MessageDigest> messageDigests = new HashMap<>();

	/**
	 * Returns the cached {@code MessageDigest} of this algorithm, creating and caching it on the first call.
	 * @return the shared digest instance
	 */
	public 	MessageDigest getMessageDigest() {
		MessageDigest messageDigest = messageDigests.get(this);
		
		boolean alreadyLoaded = messageDigest != null;
		
		if(alreadyLoaded) {
			return messageDigest;
		}

		String algorithm = this.algorithm;
		MessageDigest instance = getMessageDigest(algorithm);
		messageDigests.put(this, instance);
		return instance;
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
