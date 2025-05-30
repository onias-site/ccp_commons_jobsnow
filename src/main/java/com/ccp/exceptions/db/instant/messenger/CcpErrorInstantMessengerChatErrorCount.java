package com.ccp.exceptions.db.instant.messenger;

@SuppressWarnings("serial")
public class CcpErrorInstantMessengerChatErrorCount extends RuntimeException {
	public CcpErrorInstantMessengerChatErrorCount(long chatId) {
		super("Unable to count members in the chat id " + chatId);
	}
}
