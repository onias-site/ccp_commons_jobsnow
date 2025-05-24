package com.ccp.exceptions.json;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpJsonPathIsMissing extends RuntimeException {
	public CcpJsonPathIsMissing(CcpJsonRepresentation json) {
		super("The path is empty, please fill the missing path in the json: " + json);
	}
}
