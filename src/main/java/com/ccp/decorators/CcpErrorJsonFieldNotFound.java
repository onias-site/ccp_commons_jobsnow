package com.ccp.decorators;

@SuppressWarnings("serial")
public class CcpErrorJsonFieldNotFound extends RuntimeException {
	public CcpErrorJsonFieldNotFound(String field, CcpJsonRepresentation json) {
		super("The value is absent to the field " + field + " in json: " + json);
	}
}
