package com.ccp.especifications.email;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpContentType;

/**
 * Contract with an external e-mail provider (implemented with SendGrid), hiding its details: sends the e-mails and reads
 * the lists of addresses it suppressed.
 */
public interface CcpEmailProvider {

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

	/**
	 * Sends an e-mail with custom message headers ({@code List-Unsubscribe}, for instance). A provider that writes no
	 * real message, as the local ones, ignores the headers.
	 * @param providerToken authentication token of the provider
	 * @param providerUrl base URL of the provider
	 * @param templateId identifier of the e-mail template
	 * @param sender sender address
	 * @param subject e-mail subject
	 * @param message message body
	 * @param contentType content type (plain text or HTML)
	 * @param emailHeaders the value of each header, by its name; empty for none
	 * @param recipients recipients of the e-mail
	 * @return JSON with the result of the sending
	 */
	default CcpJsonRepresentation sendSimpleTextEmailMessage(String providerToken, String providerUrl, String templateId, String sender, String subject, String message, CcpHttpContentType contentType, CcpJsonRepresentation emailHeaders, String... recipients) {
		CcpJsonRepresentation result = this.sendSimpleTextEmailMessage(providerToken, providerUrl, templateId, sender, subject, message, contentType, recipients);
		return result;
	}

	/**
	 * Reads a whole suppression list. A failure on any page raises an error: a partial list is never returned.
	 * @param providerToken authentication token of the provider
	 * @param providerUrl base URL of the suppression lists of the provider
	 * @param suppressionList the list to read
	 * @return every record of the list, with the fields given by the provider
	 */
	List<CcpJsonRepresentation> getSuppressedEmails(String providerToken, String providerUrl, CcpEmailSuppressionList suppressionList);

	/**
	 * Reads the records of a suppression list created from a moment on. A failure on any page raises an error: a partial
	 * list is never returned.
	 * @param providerToken authentication token of the provider
	 * @param providerUrl base URL of the suppression lists of the provider
	 * @param suppressionList the list to read
	 * @param startTimeInSeconds the moment, inclusive, as a Unix timestamp in seconds
	 * @return the records created from that moment on, with the fields given by the provider
	 */
	List<CcpJsonRepresentation> getSuppressedEmailsCreatedSince(String providerToken, String providerUrl, CcpEmailSuppressionList suppressionList, long startTimeInSeconds);
}
