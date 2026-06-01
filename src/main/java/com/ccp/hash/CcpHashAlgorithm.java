package com.ccp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.ccp.decorators.CcpErrorHashAlgorithmNotFound;

public enum CcpHashAlgorithm {
	MD5("MD5"),
	SHA1("SHA1"),
	SHA256("SHA-256"),
	SHA512("SHA-512")
	;
	private final String algorithm;

	private CcpHashAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	private static HashMap<CcpHashAlgorithm, MessageDigest> messageDigests = new HashMap<>();

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

	public static MessageDigest getMessageDigest(String algorithm) {
		MessageDigest instance;
		try {
			instance = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CcpErrorHashAlgorithmNotFound(algorithm);
		}
		return instance;
	}
	
}
