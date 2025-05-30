package com.ccp.exceptions.json;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorJsonFieldNotFound extends RuntimeException {
	public CcpErrorJsonFieldNotFound(String field, CcpJsonRepresentation json) {
		super("The value is absent to the field " + field + " in json: " + json);
	}
}
