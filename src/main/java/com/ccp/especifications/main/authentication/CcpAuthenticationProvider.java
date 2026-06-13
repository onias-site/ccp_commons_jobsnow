package com.ccp.especifications.main.authentication;

/**
 * Contrato para obtenção de tokens JWT de autenticação (GCP OAuth).
 */
public interface CcpAuthenticationProvider {

	/**
	 * Obtém e retorna o token JWT atual.
	 * @return token JWT como String
	 */
	String getJwtToken();
}
