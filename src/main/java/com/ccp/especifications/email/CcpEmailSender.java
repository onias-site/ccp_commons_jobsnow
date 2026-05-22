package com.ccp.especifications.email;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpContentType;

public interface CcpEmailSender {
	
	CcpJsonRepresentation sendSimpleTextEmailMessage(String providerToken, String providerUrl, String templateId, String sender, String subject, String message, CcpHttpContentType contentType, String... recipients);
		
	
}
