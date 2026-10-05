package com.ccp.json.validations.fields.enums;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * What happens when a rule of a field is broken: {@code breakFieldValidation} stops the other validations of the field
 * (through an exception) and {@code continueFieldValidation} keeps accumulating errors.
 */
public enum CcpJsonFieldErrorHandleType {

	/** Stops the other validations of the field. */
	breakFieldValidation {
		/**
		 * Throws {@link CcpJsonFieldErrorSkipOthersValidationsToTheField} with the errors accumulated so far.
		 * @param error the accumulated errors
		 */
		public void maybeBreakValidation(CcpJsonRepresentation error) {
			CcpJsonFieldErrorSkipOthersValidationsToTheField ccpJsonFieldErrorSkipOthersValidationsToTheField = new CcpJsonFieldErrorSkipOthersValidationsToTheField(error);
			throw ccpJsonFieldErrorSkipOthersValidationsToTheField;
		}
	},
	/** Keeps validating the field. */
	continueFieldValidation {
		/**
		 * Does nothing.
		 * @param error the accumulated errors
		 */
		public void maybeBreakValidation(CcpJsonRepresentation error) {
		}
	}
	;

	/**
	 * Applies the strategy: stops or continues the validation of the field.
	 * @param error the accumulated errors
	 */
	public abstract void maybeBreakValidation(CcpJsonRepresentation error);

}
