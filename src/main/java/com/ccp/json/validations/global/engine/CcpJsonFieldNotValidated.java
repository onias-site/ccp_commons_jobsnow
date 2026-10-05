package com.ccp.json.validations.global.engine;


/** Flow-control exception thrown when a field has no recognized type annotation; caught silently to skip the field. */
@SuppressWarnings("serial")
public class CcpJsonFieldNotValidated extends RuntimeException {
	/** Builds the signal. */
	CcpJsonFieldNotValidated() {}
}
