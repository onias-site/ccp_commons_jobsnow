package com.ccp.especifications.http;

import com.ccp.decorators.CcpJsonRepresentation;

/** {@link CcpErrorHttp} of the server errors (status 5xx). */
@SuppressWarnings("serial")
public class CcpErrorHttpServer extends CcpErrorHttp{

	/**
	 * Builds the error from the details of the call.
	 * @param entity the details of the failed call
	 */
	public CcpErrorHttpServer(CcpJsonRepresentation entity) {
		super(entity);
	}


}
