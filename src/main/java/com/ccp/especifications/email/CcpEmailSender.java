package com.ccp.especifications.email;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpContentType;

/** Contract for sending e-mails through an external provider (implemented with SendGrid), hiding the provider details. */
public interface CcpEmailSender {

	/**
	 * Sends an e-mail to one or more recipients through the configured provider.
	 * @param providerToken authentication token of the provider
	 * @param providerUrl base URL of the provider
	 * @param templateId identifier of the e-mail template
	 * @param sender sender address
	 * @param subject e-mail subject
	 * @param message message body
	 * @param contentType content type (plain text or HTML)
	 * @param recipients recipients of the e-mail
	 * @return JSON with the result of the sending
	 */
	CcpJsonRepresentation sendSimpleTextEmailMessage(String providerToken, String providerUrl, String templateId, String sender, String subject, String message, CcpHttpContentType contentType, String... recipients);
		
	
}
