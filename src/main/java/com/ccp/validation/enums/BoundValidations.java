package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public interface BoundValidations {
	boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields);
	String name();
}
