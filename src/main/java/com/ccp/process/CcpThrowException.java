package com.ccp.process;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class CcpThrowException implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final RuntimeException exception;
	
	public CcpThrowException(RuntimeException exception) {
		this.exception = exception;
	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		throw this.exception;
	}

}
