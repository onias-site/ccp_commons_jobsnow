package com.ccp.json.validations.global.interfaces;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Flow-control exception that stops the global validations when a critical validator fails; carries the errors
 * accumulated so far.
 */
@SuppressWarnings("serial")
public class CcpJsonValidatorErrorBreakValidationsToTheClass extends RuntimeException {

	/** The errors accumulated until the interruption. */
	public final CcpJsonRepresentation errors;

	/**
	 * Keeps the accumulated errors.
	 * @param errors the errors accumulated so far
	 */
	CcpJsonValidatorErrorBreakValidationsToTheClass(CcpJsonRepresentation errors) {
		this.errors = errors;
	}
}
