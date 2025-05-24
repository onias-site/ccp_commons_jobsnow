package com.ccp.exceptions.http;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpHttpClientError extends CcpHttpError{

	public CcpHttpClientError(CcpJsonRepresentation entity) {
		super(entity);
	}



}
