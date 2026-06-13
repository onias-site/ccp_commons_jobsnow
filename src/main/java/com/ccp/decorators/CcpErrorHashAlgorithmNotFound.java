package com.ccp.decorators;

/**
 * Exceção lançada quando o algoritmo de hash solicitado não é reconhecido pela JVM (não encontrado via {@code MessageDigest.getInstance}).
 */
@SuppressWarnings("serial")
public class CcpErrorHashAlgorithmNotFound extends RuntimeException{
	/**
	 * Monta a mensagem indicando o nome do algoritmo não encontrado.
	 * @param algorithm o nome do algoritmo não reconhecido
	 */
	public CcpErrorHashAlgorithmNotFound(String algorithm) {
		super("Algorithm '" + algorithm + "' not found");

	}
}
