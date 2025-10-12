package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.business.CcpBusiness;

public class CcpFunctionThrowException implements CcpBusiness{

	private final RuntimeException exception;
	
	public CcpFunctionThrowException(RuntimeException exception) {
		this.exception = exception;
	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		throw this.exception;
	}

}
