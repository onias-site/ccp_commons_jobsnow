package com.ccp.json.fields.validations.exceptions;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonFieldErrorInterruptValidation extends RuntimeException{

	public final CcpJsonRepresentation error;

	public CcpJsonFieldErrorInterruptValidation(CcpJsonRepresentation error) {
		this.error = error;
	}
	
	
	
}
