package com.ccp.exceptions.db.instant.messenger;

@SuppressWarnings("serial")
public class CcpInstantMessengerChatErrorCount extends RuntimeException {
	public CcpInstantMessengerChatErrorCount(long chatId) {
		super("Unable to count members in the chat id " + chatId);
	}
}
