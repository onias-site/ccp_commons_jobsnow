package com.ccp.json.validations.global.engine;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonValidatorErrorBreakValidationsToTheClass extends RuntimeException{

	public final CcpJsonRepresentation errors;

	public CcpJsonValidatorErrorBreakValidationsToTheClass(CcpJsonRepresentation errors) {
		this.errors = errors;
	}
	
	
	
}
