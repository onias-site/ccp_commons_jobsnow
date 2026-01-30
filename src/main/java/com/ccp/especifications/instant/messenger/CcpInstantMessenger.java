package com.ccp.especifications.instant.messenger;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpInstantMessenger {

	
	Long getMembersCount(CcpJsonRepresentation parameters);
	
	CcpJsonRepresentation sendMessage(CcpJsonRepresentation parameters);
	
	String getFileName(CcpJsonRepresentation parameters);
	
	String extractTextFromMessage(CcpJsonRepresentation parameters);
	
	String getToken(CcpJsonRepresentation parameters);


}
