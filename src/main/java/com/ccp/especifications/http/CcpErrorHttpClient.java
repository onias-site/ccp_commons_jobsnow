package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;

/** {@link CcpErrorHttp} of the client errors (status 4xx). */
@SuppressWarnings("serial")
public class CcpErrorHttpClient extends CcpErrorHttp{

	/**
	 * Builds the error from the details of the call.
	 * @param entity the details of the failed call
	 */
	public CcpErrorHttpClient(CcpJsonRepresentation entity) {
		super(entity);
	}



}
