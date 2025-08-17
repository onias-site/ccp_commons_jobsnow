package com.ccp.json.fields.validations.engine;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonFieldErrorInterruptValidation extends RuntimeException{

	public final CcpJsonRepresentation validationResultFromField;

	public CcpJsonFieldErrorInterruptValidation(CcpJsonRepresentation error) {
		this.validationResultFromField = error;
	}
	
	
	
}
