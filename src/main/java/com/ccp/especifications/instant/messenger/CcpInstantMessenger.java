package com.ccp.especifications.instant.messenger;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpInstantMessenger {

	
	Long getMembersCount(CcpJsonRepresentation parameters);
	
	CcpJsonRepresentation sendMessage(CcpJsonRepresentation parameters);

	CcpJsonRepresentation sendTextMessage(String botToken, Long chatId, Long replyTo, String message);
	
	CcpJsonRepresentation sendFile(String botToken, Long chatId, Long replyTo, String fileName, String caption, Byte[] fileContent);
	
	String getFileName(CcpJsonRepresentation parameters);
	
	String extractTextFromMessage(CcpJsonRepresentation parameters);
	
	String getToken(CcpJsonRepresentation parameters);


}
