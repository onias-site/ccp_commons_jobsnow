package com.ccp.json.validations.fields.enums;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Flow-control exception thrown by a {@code breakFieldValidation} rule to skip the other validations of the current
 * field.
 */
@SuppressWarnings("serial")
public class CcpJsonFieldErrorSkipOthersValidationsToTheField extends RuntimeException {

	/** The errors accumulated until the rule was broken. */
	public final CcpJsonRepresentation validationResultFromField;

	/**
	 * Keeps the accumulated errors.
	 * @param error the errors accumulated so far
	 */
	CcpJsonFieldErrorSkipOthersValidationsToTheField(CcpJsonRepresentation error) {
		this.validationResultFromField = error;
	}
}
