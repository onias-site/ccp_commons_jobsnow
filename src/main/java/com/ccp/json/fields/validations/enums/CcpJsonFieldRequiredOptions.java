package com.ccp.json.fields.validations.enums;

import com.ccp.decorators.CcpJsonRepresentation;

public enum CcpJsonFieldRequiredOptions {

		yes {
			CcpJsonFieldStatusType getValidationStatus(String fieldName, CcpJsonRepresentation json) {
				return CcpJsonFieldStatusType.error;
			}
		}, atLeastOne {
			CcpJsonFieldStatusType getValidationStatus(String fieldName, CcpJsonRepresentation json) {
				return CcpJsonFieldStatusType.warning;
			}
		}, no {
			CcpJsonFieldStatusType getValidationStatus(String fieldName, CcpJsonRepresentation json) {
				return CcpJsonFieldStatusType.success;
			}
		}
		
		;
	
		abstract CcpJsonFieldStatusType getValidationStatus(
				String fieldName,
				CcpJsonRepresentation json
				);


		public  CcpJsonFieldStatusType validate(
				String fieldName,
				CcpJsonRepresentation json
				) {
			
			if(json.containsAllFields(fieldName)) {
				return CcpJsonFieldStatusType.success;
			}
			
			CcpJsonFieldStatusType validationStatus = this.getValidationStatus(fieldName, json);
			
			return validationStatus;
			
		}

}
