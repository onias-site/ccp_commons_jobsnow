package com.ccp.especifications.instant.messenger;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;

/** Contract for sending messages through a bot (Telegram): text messages and files. */
public interface CcpInstantMessenger {

	/**
	 * Sends a text message to a chat.
	 * @param botType identifier of the bot type
	 * @param botToken authentication token of the bot
	 * @param chatId identifier of the target chat
	 * @param replyTo identifier of the message being answered
	 * @param message the text of the message
	 * @return JSON with the result of the sending
	 */
	CcpJsonRepresentation sendTextMessage(CcpJsonFieldName botType, String botToken, Long chatId, Long replyTo, String message);

	/**
	 * Sends a binary file with a caption to a chat.
	 * @param botType identifier of the bot type
	 * @param botToken authentication token of the bot
	 * @param chatId identifier of the target chat
	 * @param replyTo identifier of the message being answered
	 * @param fileName file name
	 * @param caption file caption
	 * @param fileContent binary content of the file
	 * @return JSON with the result of the sending
	 */
	CcpJsonRepresentation sendFile(CcpJsonFieldName botType, String botToken, Long chatId, Long replyTo, String fileName, String caption, Byte[] fileContent);

}
