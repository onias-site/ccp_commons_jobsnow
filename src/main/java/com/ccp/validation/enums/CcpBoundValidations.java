package com.ccp.validation.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpBoundValidations {
	boolean isValidJson(CcpJsonRepresentation json, double bound, String... fields);
	String name();
}
