package com.ccp.exceptions.json.fields;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorJsonFieldsInvalid extends RuntimeException {

	public final CcpJsonRepresentation result;

	public CcpErrorJsonFieldsInvalid(CcpJsonRepresentation result) {
		super(result.toString());
		this.result = result;
	}
	
	public CcpJsonRepresentation getErrorAsJson() {
		String message = this.getMessage();
		CcpJsonRepresentation json = new CcpJsonRepresentation(message);
		return json;
	}
}
