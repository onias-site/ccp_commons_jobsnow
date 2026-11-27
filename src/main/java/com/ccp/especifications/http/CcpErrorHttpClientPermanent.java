package com.ccp.especifications.http;

/**
 * A client error (4xx) of an external API that was already recorded and that a new attempt would only repeat (since
 * 2026-11-27): a bot without the right to write in a group, a chat that does not exist, a malformed request. The consumer of
 * the messaging notifies it and acknowledges the message instead of rejecting it, so that the message does not come back to
 * the queue for days, holding the ones behind it.
 */
@SuppressWarnings("serial")
public class CcpErrorHttpClientPermanent extends RuntimeException {

	/** The client error of the API. */
	public final CcpErrorHttpClient clientError;

	/**
	 * Wraps the client error.
	 * @param clientError the client error of the API
	 */
	public CcpErrorHttpClientPermanent(CcpErrorHttpClient clientError) {
		super(clientError.getMessage(), clientError);
		this.clientError = clientError;
	}
}
