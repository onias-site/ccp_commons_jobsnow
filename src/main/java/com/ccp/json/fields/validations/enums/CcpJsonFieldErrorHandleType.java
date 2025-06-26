package com.ccp.json.fields.validations.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.exceptions.CcpJsonFieldErrorInterruptValidation;

public enum CcpJsonFieldErrorHandleType {

	breakFieldValidation {
		public void breakValidationIfNecessary(CcpJsonRepresentation error) {
			throw new CcpJsonFieldErrorInterruptValidation(error);
		}
	},
	continueFieldValidation {
		public void breakValidationIfNecessary(CcpJsonRepresentation error) {
		}
	}
	;
	
	public abstract void breakValidationIfNecessary(CcpJsonRepresentation error);
	
}
