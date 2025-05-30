package com.ccp.validation;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorJsonInvalid extends RuntimeException {

	public final CcpJsonRepresentation result;

	protected CcpErrorJsonInvalid(CcpJsonRepresentation result) {
		super(result.toString());
		this.result = result;
	}
	
	public CcpJsonRepresentation getErrorAsJson() {
		String message = this.getMessage();
		CcpJsonRepresentation json = new CcpJsonRepresentation(message);
		return json;
	}
}
