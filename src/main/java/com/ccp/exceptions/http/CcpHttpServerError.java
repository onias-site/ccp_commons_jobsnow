package com.ccp.exceptions.http;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpHttpServerError extends CcpHttpError{

	public CcpHttpServerError(CcpJsonRepresentation entity) {
		super(entity);
	}


}
