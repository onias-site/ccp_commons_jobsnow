package com.ccp.especifications.instant.messenger;

/**
 * Exceção lançada quando um bot Telegram tenta enviar mensagem para um usuário que o bloqueou.
 * Armazena o nome/token do bot.
 */
@SuppressWarnings("serial")
public class CcpErrorInstantMessageThisBotWasBlockedByThisUser extends RuntimeException {
	public final String botName;

	/**
	 * Armazena o token do bot em {@code botName}.
	 * @param token token ou nome do bot que foi bloqueado
	 */
	public CcpErrorInstantMessageThisBotWasBlockedByThisUser(String token) {
		this.botName = token;
	}
	
}
