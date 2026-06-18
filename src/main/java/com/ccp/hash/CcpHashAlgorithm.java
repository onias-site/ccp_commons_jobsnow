package com.ccp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Catálogo dos algoritmos de hash suportados pelo framework (MD5, SHA1, SHA256, SHA512).
 * Encapsula o nome técnico de cada algoritmo e gerencia um cache de instâncias de {@code MessageDigest} para evitar re-criação.
 */
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

	/**
	 * Retorna a instância cacheada de {@code MessageDigest} para este algoritmo.
	 * Na primeira chamada, instancia e armazena; nas seguintes, retorna do cache.
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
	 * Cria e retorna um {@code MessageDigest} para um algoritmo especificado por string.
	 * Lança {@code CcpErrorHashAlgorithmNotFound} se o algoritmo não for reconhecido pela JVM.
	 * @param algorithm o nome do algoritmo (ex.: "SHA1", "MD5")
	 */
	public static MessageDigest getMessageDigest(String algorithm) {
		MessageDigest instance;
		try {
			instance = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CcpErrorHashAlgorithmNotFound(algorithm);
		}
		return instance;
	}

	@SuppressWarnings("serial")
	public static class CcpErrorHashAlgorithmNotFound extends RuntimeException {
		private CcpErrorHashAlgorithmNotFound(String algorithm) {
			super("Algorithm '" + algorithm + "' not found");
		}
	}

}
