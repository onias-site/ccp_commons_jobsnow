package com.ccp.json.validations.fields.engine;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonFieldErrorSkipOthersValidationsToTheField extends RuntimeException{

	public final CcpJsonRepresentation validationResultFromField;

	public CcpJsonFieldErrorSkipOthersValidationsToTheField(CcpJsonRepresentation error) {
		this.validationResultFromField = error;
	}
	
	
	
}
