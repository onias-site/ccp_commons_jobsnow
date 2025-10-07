package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;

public interface CcpJsonFieldValidatorInterface {
	
	CcpJsonFieldErrorHandleType getErrorHandleType() ;
	
	String name();
	
	String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);
	
	boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	Object getRuleExplanation(Field field, CcpJsonFieldType type);

	default Object getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		String errorMessage = this.getErrorMessage(json, field, type);
		return errorMessage;
	}
	
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		boolean hasNoError = false == this.hasError(json, field, type);

		if (hasNoError) {
			return errors;
		}

		String fieldName = field.getName();
		
		Object error = this.getError(json, field, type);

		CcpJsonRepresentation updatedErrors = errors.getDynamicVersion().addToList(fieldName, error);

		CcpJsonFieldErrorHandleType errorHandleType = this.getErrorHandleType();
		
		errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}
	
	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation allRules, Field field, CcpJsonFieldType type) {

		boolean hasNoRules = false == this.hasRuleExplanation(field, type);
		
		if(hasNoRules) {
			return allRules;
		}
		
		String fieldName = field.getName();
		
		Object ruleExplanation = this.getRuleExplanation(field, type);

		CcpJsonRepresentation updatedRuleExplanation = allRules.getDynamicVersion().addToList(fieldName, ruleExplanation);
		
		return updatedRuleExplanation;
	}
	
	boolean hasRuleExplanation(Field field, CcpJsonFieldType type) ;
}
