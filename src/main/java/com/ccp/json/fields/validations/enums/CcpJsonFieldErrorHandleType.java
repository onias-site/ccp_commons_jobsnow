package com.ccp.json.fields.validations.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validations.engine.CcpJsonFieldErrorInterruptValidation;

public enum CcpJsonFieldErrorHandleType {

	breakFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
			throw new CcpJsonFieldErrorInterruptValidation(error);
		}
	},
	continueFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
		}
	}
	;
	
	public abstract void maybeBreakValidation(CcpJsonRepresentation error);
	

	
	
}
