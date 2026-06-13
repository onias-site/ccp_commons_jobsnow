package com.ccp.decorators;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.ccp.hash.CcpHashAlgorithm;

/**
 * Decorator sobre uma string que oferece operações de hash criptográfico (MD5, SHA1, SHA256, SHA512).
 * Converte o conteúdo interno em bytes e aplica o algoritmo especificado, retornando o resultado como {@code BigInteger} ou string hexadecimal.
 */
public class CcpHashDecorator implements CcpDecorator<String> {
	public final String content;

	/**
	 * Encapsula a string a ser hasheada.
	 * @param content a string a ser hasheada
	 */
	protected CcpHashDecorator(String content) {
		this.content = content;
	}

	public String toString() {
		return this.content;
	}

	
	/**
	 * Aplica o algoritmo de hash e retorna o resultado como string hexadecimal em minúsculas.
	 * @param algorithm o algoritmo de hash a aplicar
	 */
	public String asString(CcpHashAlgorithm algorithm) {
		BigInteger bi = this.asBigInteger(algorithm);

		String strHash = bi.toString(16).toLowerCase();

		return strHash;
	}
	
	/**
	 * Aplica o algoritmo de hash e retorna o resultado como {@code BigInteger} (útil para operações numéricas sobre o hash).
	 * @param algorithm o algoritmo de hash a aplicar
	 */
	public BigInteger asBigInteger(CcpHashAlgorithm algorithm) {
		MessageDigest digest = algorithm.getMessageDigest();
		
		byte[] bytes = this.content.getBytes(StandardCharsets.UTF_8);
		byte[] hash = digest.digest(bytes);
		BigInteger bi = new BigInteger(hash);
		return bi;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna a string original.
	 */
	public String getContent() {
		return this.content;
	}

}
