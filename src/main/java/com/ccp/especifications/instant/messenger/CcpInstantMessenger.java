package com.ccp.especifications.instant.messenger;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Contrato para envio de mensagens via bot (Telegram). Suporta envio de texto e envio de arquivos.
 */
public interface CcpInstantMessenger {

	/**
	 * Envia mensagem de texto para um chat.
	 * @param botType identificador do tipo do bot
	 * @param botToken token de autenticação do bot
	 * @param chatId identificador do chat destinatário
	 * @param replyTo identificador da mensagem a responder (pode ser null)
	 * @param message texto da mensagem
	 * @return JSON com o resultado do envio
	 */
	CcpJsonRepresentation sendTextMessage(CcpJsonFieldName botType, String botToken, Long chatId, Long replyTo, String message);

	/**
	 * Envia arquivo binário com legenda para um chat.
	 * @param botType identificador do tipo do bot
	 * @param botToken token de autenticação do bot
	 * @param chatId identificador do chat destinatário
	 * @param replyTo identificador da mensagem a responder (pode ser null)
	 * @param fileName nome do arquivo
	 * @param caption legenda do arquivo
	 * @param fileContent conteúdo binário do arquivo
	 * @return JSON com o resultado do envio
	 */
	CcpJsonRepresentation sendFile(CcpJsonFieldName botType, String botToken, Long chatId, Long replyTo, String fileName, String caption, Byte[] fileContent);

}
