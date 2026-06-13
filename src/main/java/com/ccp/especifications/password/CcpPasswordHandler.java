package com.ccp.especifications.password;

/**
 * Contrato para hash e verificação de senhas (BCrypt via Mindrot).
 */
public interface CcpPasswordHandler {

	/**
	 * Verifica se a senha em texto plano corresponde ao hash.
	 * @param password senha em texto plano
	 * @param hash hash BCrypt armazenado
	 * @return true se a senha corresponde ao hash
	 */
	boolean matches(String password, String hash);

	/**
	 * Gera e retorna o hash BCrypt da senha.
	 * @param password senha em texto plano
	 * @return hash BCrypt da senha
	 */
	String getHash(String password);

}
