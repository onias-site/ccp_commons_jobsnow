package com.ccp.exceptions.json;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonFieldNotFound extends RuntimeException {
	public CcpJsonFieldNotFound(String field, CcpJsonRepresentation json) {
		super("The value is absent to the field " + field + " in json: " + json);
	}
}
