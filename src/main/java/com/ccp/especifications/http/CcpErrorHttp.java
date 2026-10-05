package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTextDecorator;

/**
 * Raised when an HTTP call returns a status that has no flow; carries the request, the response and the expected
 * statuses. Subclassed by {@link CcpErrorHttpClient} (4xx) and {@link CcpErrorHttpServer} (5xx).
 */
@SuppressWarnings("serial")
public class CcpErrorHttp extends RuntimeException {
	/** The details of the call: url, method, headers, request, status, response, trace, details and expectedStatusList. */
	public final CcpJsonRepresentation entity;
	/**
	 * Builds the error from the details of the call.
	 * @param entity the details of the call
	 */
	protected CcpErrorHttp(CcpJsonRepresentation entity) {
		super(getMessage(entity));
		this.entity = entity;
	}
	/**
	 * Builds the message with the trace, the details and the expected statuses.
	 * @param entity the details of the call
	 * @return the message
	 */
	private static String getMessage(CcpJsonRepresentation entity) {
		String string = "\n\n\nTrace:{trace}\nDetails: {details}\n. All expected status: {expectedStatusList}";
		com.ccp.decorators.CcpStringDecorator ccpStringDecorator = new com.ccp.decorators.CcpStringDecorator(string);
		CcpTextDecorator ccpStringDecoratorText = ccpStringDecorator.text();
		var resolveTemplate = ccpStringDecoratorText.resolveTemplate(entity);
		String message = resolveTemplate.content;
		return message;
	}
}
