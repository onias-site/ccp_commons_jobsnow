package com.ccp.json.validations.fields.enums;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.engine.CcpJsonFieldErrorSkipOthersValidationsToTheField;

public enum CcpJsonFieldErrorHandleType {

	breakFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
			throw new CcpJsonFieldErrorSkipOthersValidationsToTheField(error);
		}
	},
	continueFieldValidation {
		public void maybeBreakValidation(CcpJsonRepresentation error) {
		}
	}
	;
	
	public abstract void maybeBreakValidation(CcpJsonRepresentation error);
	

	
	
}
