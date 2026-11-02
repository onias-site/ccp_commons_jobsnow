package com.ccp.especifications.http;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Hides the credentials of an HTTP call in everything that outlives it: the curl of the response and the details of the
 * error, which go to logs and to the database. Until 2026-11-01 only the curl was masked, so every failed call carried
 * its {@code Authorization} header and the Telegram bot token in clear. The body is masked too, for the credentials that
 * travel in it, such as the secret of a Telegram webhook.
 */
public final class CcpHttpSecretMask {
	/** The single instance: the class keeps no state. */
	public static final CcpHttpSecretMask INSTANCE = new CcpHttpSecretMask();

	private CcpHttpSecretMask() {}

	/** Texts of the mask. */
	public enum Fields implements CcpJsonFieldName {
		/** The text that replaces a secret. */
		MASK { public String getValue() { return "***"; } },
		/** The Telegram bot token, which travels in the URL path. */
		BOT_TOKEN_IN_THE_PATH { public String getValue() { return "/bot[^/]+/"; } },
		/** The URL path with the bot token hidden. */
		MASKED_BOT_TOKEN { public String getValue() { return "/bot***/"; } },
		/** The secret of a Telegram webhook, which travels in the JSON body of {@code setWebhook}. */
		WEBHOOK_SECRET_IN_THE_BODY { public String getValue() { return "\"secret_token\"\s*:\s*\"[^\"]*\""; } },
		/** The body with the secret of the webhook hidden. */
		MASKED_WEBHOOK_SECRET { public String getValue() { return "\"secret_token\":\"***\""; } }
	}

	/** Headers whose values are credentials, in lower case. */
	enum SecretHeaders implements CcpJsonFieldName {
		/** The {@code Authorization} header. */
		authorization,
		/** The {@code Proxy-Authorization} header. */
		proxy_authorization { public String getValue() { return "proxy-authorization"; } },
		/** The {@code Cookie} header. */
		cookie,
		/** The session token header of the platform. */
		sessiontoken,
		/** The {@code X-Api-Key} header. */
		x_api_key { public String getValue() { return "x-api-key"; } }
	}

	/**
	 * Hides the value of a header when it is a credential.
	 * @param headerName the header name, in any case
	 * @param headerValue the header value
	 * @return the mask for a credential, the same value otherwise
	 */
	public String maskHeaderValue(String headerName, String headerValue) {
		boolean secretHeader = this.isSecretHeader(headerName);
		String mask = Fields.MASK.getValue();
		String maskedValue = secretHeader ? mask : headerValue;
		return maskedValue;
	}

	/**
	 * Hides the values of the headers that are credentials.
	 * @param headers the headers
	 * @return the same headers, with the credentials hidden
	 */
	public CcpJsonRepresentation maskHeaders(CcpJsonRepresentation headers) {
		String mask = Fields.MASK.getValue();
		CcpJsonRepresentation maskedHeaders = headers;
		Set<String> headerNames = headers.fieldSet();

		for (String headerName : headerNames) {
			boolean notSecretHeader = false == this.isSecretHeader(headerName);

			if(notSecretHeader) {
				continue;
			}
			CcpFieldName headerField = new CcpFieldName(headerName);
			maskedHeaders = maskedHeaders.put(headerField, mask);
		}
		return maskedHeaders;
	}

	/**
	 * Hides the Telegram bot token that travels in the URL path.
	 * @param url the URL
	 * @return the URL with the token hidden
	 */
	public String maskUrl(String url) {
		String botTokenInThePath = Fields.BOT_TOKEN_IN_THE_PATH.getValue();
		String maskedBotToken = Fields.MASKED_BOT_TOKEN.getValue();
		String maskedUrl = url.replaceAll(botTokenInThePath, maskedBotToken);
		return maskedUrl;
	}

	/**
	 * Hides the credentials that travel in a request body.
	 * @param body the request body
	 * @return the body with the credentials hidden
	 */
	public String maskBody(String body) {
		String webhookSecretInTheBody = Fields.WEBHOOK_SECRET_IN_THE_BODY.getValue();
		String maskedWebhookSecret = Fields.MASKED_WEBHOOK_SECRET.getValue();
		String maskedBody = body.replaceAll(webhookSecretInTheBody, maskedWebhookSecret);
		return maskedBody;
	}

	/**
	 * Tells whether a header holds a credential, ignoring the case of its name.
	 * @param headerName the header name
	 * @return {@code true} for a credential
	 */
	private boolean isSecretHeader(String headerName) {
		String lowerCaseHeaderName = headerName.toLowerCase();
		SecretHeaders[] secretHeaders = SecretHeaders.values();
		Stream<SecretHeaders> secretHeadersStream = Arrays.stream(secretHeaders);
		Set<String> secretHeaderNames = secretHeadersStream.map(x -> x.getValue()).collect(Collectors.toSet());
		boolean secretHeader = secretHeaderNames.contains(lowerCaseHeaderName);
		return secretHeader;
	}
}
