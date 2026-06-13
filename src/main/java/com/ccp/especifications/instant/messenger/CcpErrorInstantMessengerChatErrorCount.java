package com.ccp.especifications.instant.messenger;

/**
 * Exceção lançada quando não é possível contar membros de um chat Telegram.
 */
@SuppressWarnings("serial")
public class CcpErrorInstantMessengerChatErrorCount extends RuntimeException {

	/**
	 * Inclui o chatId na mensagem de erro.
	 * @param chatId identificador do chat onde a contagem falhou
	 */
	public CcpErrorInstantMessengerChatErrorCount(long chatId) {
		super("Unable to count members in the chat id " + chatId);
	}
}
