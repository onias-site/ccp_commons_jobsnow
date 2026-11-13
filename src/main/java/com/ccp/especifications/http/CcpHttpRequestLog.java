package com.ccp.especifications.http;

import java.util.List;

import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contract with the log of the HTTP requests answered by a published service (implemented with the request log that the
 * cloud provider keeps for each service), hiding its details: tells which requests reached the service, with which
 * status, from which client.
 */
public interface CcpHttpRequestLog {

	/**
	 * Reads the requests answered by a service from a moment on, oldest first. A failure on any page raises an error: a
	 * partial list is never returned.
	 * @param serviceName the name of the published service
	 * @param method the HTTP method of the requests
	 * @param pathRegex a regular expression that the URL of the request must contain (RE2 syntax)
	 * @param httpStatus the status answered by the service
	 * @param startTimeInMillis the moment, inclusive, in milliseconds since the epoch
	 * @return one JSON for each request, with the fields of {@link Fields}; a field the log did not keep is absent
	 */
	List<CcpJsonRepresentation> getRequestsSince(String serviceName, CcpHttpMethods method, String pathRegex, int httpStatus, long startTimeInMillis);

	/** The fields of each request returned by {@link CcpHttpRequestLog#getRequestsSince}. */
	enum Fields implements CcpJsonFieldName {
		/** The full URL of the request. */
		url,
		/** The user agent of the client. */
		userAgent,
		/** The IP address of the client. */
		ip,
		/** The moment of the request, in milliseconds since the epoch. */
		timestamp
	}
}
