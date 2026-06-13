package com.ccp.especifications.email;

import java.util.List;

/**
 * Exceção lançada quando endereços de e-mail fornecidos são inválidos.
 * Carrega a lista de endereços inválidos na mensagem.
 */
@SuppressWarnings("serial")
public class CcpErrorEmailInvalidAdresses extends RuntimeException{

	/**
	 * Recebe a lista de endereços inválidos e os inclui na mensagem de erro.
	 * @param invalidEmails lista de endereços de e-mail inválidos
	 */
	public CcpErrorEmailInvalidAdresses(List<?> invalidEmails) {
		super("These following mail addresses are not valid: " + invalidEmails);
	}
}
