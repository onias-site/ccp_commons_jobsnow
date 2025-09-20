package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;

public interface CcpJsonFieldValidatorInterface {
	
	CcpJsonFieldErrorHandleType getErrorHandleType() ;
	
	String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);
	
	boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	CcpJsonRepresentation getRule(Field field, CcpJsonFieldType type);

	default Object getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		
		String errorMessage = this.getErrorMessage(json, field, type);
		return errorMessage;
	}
	
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		boolean hasNoError = false == this.hasError(json,  field, type);

		if (hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		String fieldName = field.getName();

		Object error = this.getError(json, field, type);

		CcpJsonRepresentation updatedErrors = errors.getDynamicVersion().addToList(fieldName, error);

		CcpJsonFieldErrorHandleType errorHandleType = this.getErrorHandleType();
		
		errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}

}
