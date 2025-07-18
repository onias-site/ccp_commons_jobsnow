package com.ccp.json.fields.validations.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.json.fields.validations.exceptions.CcpJsonFieldErrorInterruptValidation;

public enum CcpJsonFieldErrorHandleType {

	breakFieldValidation {
		public void breakValidation(CcpJsonRepresentation error) {
			throw new CcpJsonFieldErrorInterruptValidation(error);
		}
	},
	continueFieldValidation {
		public void breakValidation(CcpJsonRepresentation error) {
		}
	}
	;
	
	public abstract void breakValidation(CcpJsonRepresentation error);
	

	
	
}
