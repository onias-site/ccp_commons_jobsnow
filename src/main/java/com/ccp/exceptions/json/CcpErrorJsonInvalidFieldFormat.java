package com.ccp.exceptions.json;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorJsonInvalidFieldFormat extends RuntimeException {
	
	public CcpErrorJsonInvalidFieldFormat(Object value, String fieldName, String fieldType, CcpJsonRepresentation json) {
		super("The value '" + value + "' from the field '" + fieldName + " is not a '" + fieldType + "' in the following json: " + json);
	}
	
}
